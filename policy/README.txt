How to execute rsbc-ride-policies.yaml:

oc process -f rsbc-ride-policies.yaml -p NAMESPACE_PREFIX=be5301 -p ENVIRONMENT=dev |oc apply -n be5301-dev -f -
oc process -f rsbc-ride-policies.yaml -p NAMESPACE_PREFIX=be5301 -p ENVIRONMENT=test |oc apply -n be5301-test -f -
oc process -f rsbc-ride-policies.yaml -p NAMESPACE_PREFIX=be5301 -p ENVIRONMENT=prod |oc apply -n be5301-prod -f -
