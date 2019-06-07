# SDK Security filtering
## Storage changes
![Storage changes](security_filtering_resources/storage_changes.png)

- In the old approach, content documents and ACLs were stored in the same collection.
- In the new approach, access control entities (users,groups,ACLs) are stored in an separate collection.

### Content collection

- This collection the contains content documents produced by a datasource.
- This collection does not contain ACLs.

### Access control collection

- Contains users, groups and document ACLs.
- The permission hierarchy is represented by a graph of normalized access control and document ACL entities.
- Types of entities:
    - Access control: Represents a group, user, role, etc.
    - Document ACL: An entity which contains a reference to a content document, and associated permission hierarchy.

## Access Control collection - Data model
### Access control entity

| Field | Description |
| ------- | ------- |
| id | ID of the access control |
| type_s | Type of access control (group, user, role assignment, role definition,, etc) |
| label_s | Description of the access control |
| parents_ss | Outbound edges, i.e. parent objects can be represented with this field |
| owned_by_ss | Inbound edges i.e. list of access controls which are owned by the current access control |

### Document ACL entity

| Field | Description |
| ------- | ------- |
| id | ID of the content document |
| type_s | A document ACL is an access control too, but with type “acl”. |
| label_s | Description of the document ACL |
| parents_ss | Outbound edges, not commonly used |
| owned_by_ss | Inbound edges, i.e. used for inheriting permissions |

### Sample
#### Diagram representation
![Diagram representation](security_filtering_resources/diagram_representation.png)

#### Graph representation
![Graph representation](security_filtering_resources/graph_representation.png)

## Indexing time
### Contents / Documents indexing

- Documents are indexed to the content collection. Note, there is no field ACLs field (`acl_ss`).

```java
    ctx.emitContent(<STREAM_SUPPLIER>, <FIELDS>);
```

```java
    ctx.emitDocument(<FIELDS>);
```

### Access controls / Document ACLs indexing

- Normalized ACLs are stored in the access control collection.

```java
    ctx.emitGroup(
        <GROUP_ID>,
        <LABEL>,
        <OPERATION>,
        <PARENTS>
    );
```

```java
    ctx.emitUser(
        <USER_ID>,
        <LABEL>,
        <OPERATION>,
        <PARENTS>
    );
```

- Only documents or contents can index their ACLs

```java
    ctx.emitDocumentACL(
        <DOCUMENT_ID>,
        <OPERATION>,
        <OWNED_BY>
    );
```

### Supported operations

- Add an access control
- Update an access control
- Delete an access control

## Querying time

- Graph and join query parsers are used together to build a security filter query.
- The input for building the query is typically a user ID.
- Using the input (user ID), the graph query parser traverses linked access control documents (groups, roles etc.)
- The Join query parser is used to join the access control collection and the content collection.
- ACLs produced by the query are used to select the associated content documents into the final query result.

### Sample query

#### Old filter query sample
```
{!lucene q.op=OR} acl_ss:”user_2” acl_ss:”group_4” acl_ss:”group_3” ...
```

#### New filter query sample
```
{!join from=id to=_lw_source_id_s fromIndex=<ACCESS_CONTROL_COLL>}{!graph from=owned_by_ss to=parents_ss returnOnlyLeaf=true}id:"user_1"
```
## Required components

- Plugins requiring security filtering must implement the `AccessControlFetcher` component and `SecurityFilter` component.

### AccessControlFetcher sample component

```java
public class SecurityFilteringAccessControlFetcher implements AccessControlFetcher {
  
  private static final Logger logger = LogManager.getLogger(SecurityFilteringAccessControlFetcher.class);
  
  private final SecurityFilteringConfig config;
  private final Random random;
  
  @Inject
  public SecurityFilteringAccessControlFetcher(
      SecurityFilteringConfig config
  ) {
    this.config = config;
    this.random = new Random();
  }
  
  @Override
  public FetchResult fetch(FetchContext ctx) {
    ...
  }
}
```

See [SecurityFilteringAccessControlFetcher](connectors/security-filtering-connector/src/main/java/com/lucidworks/fusion/connector/plugin/fetcher/SecurityFilteringAccessControlFetcher.java) for additional details.

### SecurityFilter sample component

```java
public class SecurityFilteringSecurityFilterComponent implements SecurityFilterComponent {

  private final SecurityFilterBuilder builder;

  @Inject
  public SecurityFilteringSecurityFilterComponent(SecurityFilterBuilder builder) {
    this.builder = builder;
  }


  @Override
  public SecurityFilter buildSecurityFilter(Subject subject) {
    return builder.withAccessControl(subject.getPrincipal()).build();
  }
}
```

See [SecurityFilteringSecurityFilterComponent](connectors/security-filtering-connector/src/main/java/com/lucidworks/fusion/connector/plugin/security/SecurityFilteringSecurityFilterComponent.java) for additional details.

- Components must be registered in the plugin, i.e.

```
    return builder(SecurityFilteringConfig.class)
        .withFetcher(CONTENT, SecurityFilteringContentFetcher.class, nonGenModule)
        .withFetcher(ACCESS_CONTROL, SecurityFilteringAccessControlFetcher.class, nonGenModule)
        .withSecurityFilter(SecurityFilteringSecurityFilterComponent.class, nonGenModule)
        .build();

```

See [SecurityFilteringPlugin](connectors/security-filtering-connector/src/main/java/com/lucidworks/fusion/connector/plugin/SecurityFilteringPlugin.java) for additional details.