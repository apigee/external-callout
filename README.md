# ExternalCallout policy

_This is a preview of the GitHub repo to accompany Apigee's ExternalCallout
policy, so the current content is limited and subject to change._

The ExternalCallout policy allows users to build their own services using a
well-defined API that is supported by Apigee. The API and services are defined
using [Protocol Buffers](https://developers.google.com/protocol-buffers) and
[gRPC](https://grpc.io/). The policy enables users to add a policy within the
proxy flow to send gRPC requests to their gRPC servers to implement custom
behavior. See [the official Apigee
documentation](https://cloud.google.com/apigee/docs/api-platform/reference/policies/external-callout-policy)
for more information about the policy.

This repository contains files that may be useful for developers as they are
developing their ExternalCallout policies. More specifically, the repository
contains following:

- [proto](proto): The proto that defines the API used by Apigee and the gRPC
  server to communicate.
- [server-stubs](server-stubs): gRPC server stubs that users can use to quickly
  create and deploy a gRPC server. Currently available Java, with support for
  Golang and Node.js coming soon.
- sample-proxies: Sample proxies that users can use to try out the
  ExternalCallout policy. Coming soon.

## License

This content of this project is provided under the [Apache
2.0](https://www.apache.org/licenses/LICENSE-2.0) license. See the
[LICENSE](/LICENSE) file for terms and conditions.

## Support

Issues filed on GitHub are not subject to service level agreements (SLA's) and
responses should be assumed to be on an ad-hoc volunteer basis. The [Apigee
community board](https://community.apigee.com/) is recommended for community
support and is regularly checked by Apigee experts.

Apigee customers should use [formal support
channels](https://cloud.google.com/apigee/support).
