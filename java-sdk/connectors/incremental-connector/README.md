# Random Content Incremental Connector

## Connector Description

This connector generates a configurable number of documents all with random titles and body fields.
In the first job, a number of documents are generated. In the second, and subsequent jobs, the connector will generate also an specific number of new documents.

This connector emits checkpoints and emit candidates as transient=true.

Crawls are incremental.

## Quick start

1. Clone the repo:
```
git clone https://github.com/lucidworks/connectors-sdk-resources.git
cd connectors-sdk-resources/java-sdk/connectors/
./gradlew assemblePlugins
```

2. This produces one zip file, named `simple-connector-incrementa.zip`, located in the `build/plugins` directory.
This artifact is now ready to be uploaded directly to Fusion as a connector plugin.

3. See the following [instructions](../README.md) on how to build, deploy, and run the plugin

## Connector properties

### Main properties

|Property Name| Property description|
|---|---|
| Incremental total | the number of documents to generate in the second and subsequent jobs |

### Random content generator properties

|Property Name| Property description|
|---|---|
| Total | The total number of documents to generate |
| Minimum number of sentences | The minimum number of sentences to generate per document, the random generator will use this value as lower bound to calculate a random number of sentences|
| maximum number of sentences | The maximum number of sentences to generate per document, the random generator will use this value as upper bound to calculate a random number of sentences|


## How to use the connector

- Create a configuration with the properties listed above.
- After the first job is completed, the connector will index the same number of documents as defined in the `Random content generator properties.Total` property.
- The second and subsesquen jobs will index new documents as set in the `Incremental total` property.