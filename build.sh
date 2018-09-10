#!/bin/bash
curl -H 'PRIVATE-TOKEN: $token' 'https://gitlab.com/api/v4/projects/8312758/repository/files/twrpbuilderkey.jks/raw?ref=master' -o twrpbuilderkey.jks
curl -H 'PRIVATE-TOKEN: $token' 'https://gitlab.com/api/v4/projects/8312758/repository/files/keystore.properties/raw?ref=master' -o keystore.properties
chmod 777 twrpbuilderkey.jks
chmod 777 keystore.properties
mv twrpbuilderkey.jks app/