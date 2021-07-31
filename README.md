# Vendor Release API

![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/sdkman/vendor-release)

Used by vendors for releasing new candidate versions on SDKMAN!

### Test

    $ sbt acc:test

### Run locally

    $ docker run --rm -d -p="27017:27017" --name=mongo mongo:3.2
    $ sbt run
