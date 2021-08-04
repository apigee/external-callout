# Server stub: Java

This example will go over how to set up a gRPC server in Java in Cloud Run that
modifies the response flow variable by adding a header and modifying the
content and how to set up an ExternalCallout policy that uses this gRPC server.

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

The External Callout policy requires a running gRPC server, the gRPC server be
registered with Apigee, and the External Callout policy itself. The instructions
are broken down into the following parts:

1. [Set up the gRPC server in Cloud Run](#part-1-set-up-the-grpc-server-in-cloud-run)
1. [Set up the TargetServer in Apigee](#part-2-set-up-the-targetserver-in-apigee)
1. [Set up the proxy in Apigee](#part-3-set-up-the-proxy-in-apigee)

### Part 1: Set up the gRPC server in Cloud Run

1. Clone this repo.

   ```
   https://github.com/apigee/external-callout.git
   ```

1. Change to the Java server stub directory.

   ```
   cd external-callout/server-stubs/java
   ```

1. Fill in TODO's within `cloudbuild.yaml` (and optionally, `GrpcServer`).

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
       following permissions to $GCP_PROJECT_ID@cloudbuild.gserviceaccount.com:
        1. Cloud Build Service Agent: permission to build on Cloud Build
        1. Storage Object Admin: permission to modify storage objects
        1. Cloud Run Admin: permission required for Cloud Run (see
           [here](https://cloud.google.com/build/docs/deploying-builds/deploy-cloud-run#before_you_begin))
        1. Service Account User: permission required for Cloud Run (see
           [here](https://cloud.google.com/build/docs/deploying-builds/deploy-cloud-run#before_you_begin))
    1. Wait for permissions to propagate.
    1. Check the current project configuration. is using the appropriate
       project.
       ```
       gcloud config list
       ```
       "project" should list the desired project. If it is not correct, run the
       following:
       ```
       gcloud config set project $PROJECT
       ```
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

You've successfully set up a gRPC server on Cloud Run! Now, let's move onto
setting up the TargetServer.

### Part 2: Set up the TargetServer in Apigee

1. Go to the Apigee UI at https://apigee.google.com.
1. Go to "Admin > Environments > Target Servers".
1. Create a new target server with the following fields:
    1. "Enabled": Check the box
    1. "Name": Name of the target server (ex. "my-grpc-server")
    1. "Host": The URL of your Cloud Run server without "https://"
    1. "Protocol": Choose "GRPC"
    1. "Port": 443
    1. "SSL": Check the box to enable; no other fields are required. Even though
       the Cloud Run server has been configured to accept unauthenticated
       requests, Cloud Run redirects all HTTP requests to HTTPS and requires
       TLS. This is handled by Google's certificate for the default domain
       (*.a.run.app), so no other keystores, truststores, etc. are required
       during this configuration. TLS will then be terminated before reaching
       the gRPC server. See the [Cloud Run
       documentation](https://cloud.google.com/run/docs/triggering/https-request#creating_public_services) and this [Cloud Run
       FAQ](https://github.com/ahmetb/cloud-run-faq#does-cloud-run-offer-ssltls-certificates-https)
       for more information.

You've successfully set up your Cloud Run gRPC server as a TargetServer in
Apigee! Now, let's move onto setting up the proxy.

### Part 3: Set up the proxy in Apigee

1. Go to the Apigee UI at https://apigee.google.com.
1. Go to "Develop > API Proxies" and create a new proxy (no target needed).
1. Create a new External Callout policy on the response preflow with the
   following XML (replace $TARGET_SERVER_NAME with the name used in Part 2):
    ```
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <ExternalCallout continueOnError="false" enabled="true" name="External-Callout-1">
        <GrpcConnection>
            <Server name="$TARGET_SERVER_NAME"/>
        </GrpcConnection>
        <TimeoutMs>5000</TimeoutMs>
    </ExternalCallout>
    ```
1. Save and deploy your proxy.
1. Send a request to your proxy via curl, trace, etc.
    ```
    curl -v https://$HOST_NAME/$PROXY_ENDPOINT
    ```
    You should receive a response with header "grpc.server.header" equal to
    "Java" and the response body equal to "Response from the gRPC server
    (Java)."

Congrats! You have now created a functioning External Callout policy.
