#!/bin/bash

VERSION_NAME=$(grep "^VERSION_NAME=" gradle.properties | cut -d '=' -f 2)
          if [[ $VERSION_NAME == *-SNAPSHOT ]]; then
            echo "::set-output name=is_snapshot::true"
          else
            echo "::set-output name=is_snapshot::false"
          fi
