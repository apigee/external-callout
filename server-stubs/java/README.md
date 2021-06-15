# Server stub: Java

This example will go over how to set up a gRPC server in Java in Cloud Run that
modifies the response flow variable by adding a header and modifying the
content.

Note: This sets up a server in Cloud Run that accepts unauthenticated requests.
Support and instructions for authentication are to come.

## Prerequisites

- A GCP project provisioned with either:
  - Apigee Hybrid 1.5 or later, or
  - Apigee X
- A device with the following software installed:
  - maven
  - [gcloud](https://cloud.google.com/sdk/docs/quickstart)

## Instructions

1. Clone this repo.

   ```
   https://github.com/apigee/external-callout.git
   ```

1. Change to the Java server stub directory.

   ```
   cd external-callout/server-stubs/java
   ```

1. Fill in TODO's within the code.

   The server is set up on Cloud Run using a `cloudbuild.yaml` file. You must
   fill in the TODO's to replace `IMAGE_NAME`, `SERVICE_NAME`, and `REGION` with
   those of your GCP project.

   Optionally, you can modify what the gRPC server does by modifying the TODO in
   `GrpcServer.processMessage`.

1. _Optional_: Test the server locally
    1. Compile the server and client code. In `java/`:
       ```
       mvn clean install
       ```
    1. Start the server. This uses `localhost:8080` by default. In
       `java/target/`:
       ```
       java -cp sample-app-1-jar-with-dependencies.jar com.sample.server.GrpcServer
       ```
    1. Send a request with the client. In `java/target/` in another window:
       ```
       java -cp sample-app-1-jar-with-dependencies.jar com.sample.client.GrpcClient localhost 8080
       ```
    1. You should see the following response printed from the gRPC server.
       ```
       Sending a request to localhost:8080...
       response {
         headers {
           key: "grpc.server.header"
           value {
             strings: "Java"
           }
         }
         content: "Response from the gRPC server (Java)."
       }
       ```

1. Upload the server code to Cloud Run.
    1. Enable Cloud Build API on GCP project
    1. Enable permissions. In GCP, go to "IAM & Admin &gt; IAM" and add the
       following permissions to {project ID}@cloudbuild.gserviceaccount.com:
        1. Cloud Build Service Agent: permission to build on Cloud Build
        1. Storage Object Admin: permission to modify storage objects
        1. Cloud Run Admin: permission required for Cloud Run (see
           [here](https://cloud.google.com/build/docs/deploying-builds/deploy-cloud-run#before_you_begin))
        1. Service Account User: permission required for Cloud Run (see
           [here](https://cloud.google.com/build/docs/deploying-builds/deploy-cloud-run#before_you_begin))
    1. Wait for permissions to propagate.
    1. Submit the code to Cloud Run. This may take up to a few minutes. In
       `java/`:
       ```
       gcloud builds submit
       ```
       Once this step is finished, you should see the `gcloud` command end in
       "SUCCESS".
1. _Optional_: Test the Cloud Run server with a local client, `GrpcClient`.
    1. If the server is running on CloudRun, use "host"={URL without https://}
       and port=443. You can find the host on GCP in the Cloud Run section or in
       the terminal window where you deployed the service (look for "Service
       URL:").
    1. Send a request to the Cloud Run server. In `java/target/` (make sure
       you've run `mvn clean install`):
       ```
       java -cp sample-app-1-jar-with-dependencies.jar com.sample.client.GrpcClient [host] [port]
       ```
    1. You should see a response like the following
       ```
       Sending a request to {gRPC host}:443...
       response {
         headers {
           key: "grpc.server.header"
           value {
             strings: "Java"
           }
         }
         content: "Response from the gRPC server (Java)."
       }
       ```

// TODO(b/191119202): Add more detailed instructions for the next part.

1. You finished setting up a gRPC server on Cloud Run! Now, follow the
   instructions for setting up the remaining Apigee components.
    1. Create a target server with protocol "GRPC"
    1. Deploy a proxy with ExternalCallout policy in the response preflow.
    1. Send a request to the proxy, and you should see the response with the
       modified header and content.
