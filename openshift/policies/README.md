# RSBC-RIDE Network Policies

This readme summarizes the network policies required for the RIDE (RSBC-RIDE) to operate in OpenShift 4.x namespaces.

## Policies

### deny-by-default

    The default posture for a security first namespace is to
    deny all traffic. If not added this rule will be added
    by Platform Services during environment cut-over.

### allow-from-openshift-ingress

    This policy allows any pod with a route & service combination
    to accept traffic from the OpenShift router pods. This is
    required for things outside of OpenShift (like the Internet)
    to reach your pods.

### allow-all-internal

    Allow all pods within the current namespace to communicate
    to one another.

## Applying Default RSBC-RIDE network policies

`` oc process -f rsbc-ride-policies.yaml -p NAMESPACE_PREFIX=<NAMESPACE_PREFIX_HERE> -p ENVIRONMENT=<ENVIRONMENT_NAME_HERE> | oc -n <NAMESPACE_PREFIX_HERE>-<ENVIRONMENT_NAME_HERE> apply -f - ``

e.g., applying the network policies in the be5301-dev namespace (RSBC Hub Dev Environment):

`` oc process -f RSBC-RIDE-policies.yaml -p NAMESPACE_PREFIX=be5301 -p ENVIRONMENT=dev | oc -n be5301-dev apply -f - ``
