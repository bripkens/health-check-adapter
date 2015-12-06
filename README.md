<h1 align="center">Health Check Adapter</h1>
<p align="center">Send health check changes to Slack</p>

[![](https://badge.imagelayers.io/bripkens/health-check-adapter:latest.svg)](https://imagelayers.io/?images=bripkens/health-check-adapter:latest 'Get your own badge on imagelayers.io')

This application connects to one or more applications and
continuously calls the applications' health check endpoint to determine
the applications' health. When the health status changes, it will send
a message to a Slack channel.

<p align="center">
  <img src="./screenshot.png"
       alt="Screenshot showing the Slack integration"/>
</p>

*We are living in a weird world: This tool is not affiliated with Dr. Mario in any way and copyright belongs to Nintendo. You probably knew that, but you know how it is…*

## How It Works
The mechanism is currently very simple:

 - An actor is started for every configured component. These actors will
   continuously poll the components' health check endpoints in the
   configured interval.
 - A component is determined to be…
   - *healthy* when the health check endpoint returns a `200` HTTP status code,
   - *not reachable* when a connection cannot be established or the health
     check does not send a response within two seconds or
   - *unhealthy* in all other cases. 
 - When an identified component's health changes, a message is send to
   its designated reporting actor. Reporting actors are named and it is
   possible to use varying reporting strategies per component.
 - The reporting actor compares the component health to the previous health
   and executes its reporting action, e.g. sends a message to Slack or
   prints to the console.

## Usage
There is no ready made distribution of mechanism for this health check
adapter in place. It would be neat to distribute this adapter as a Docker
image. If this sounds useful to you, get in touch and we can discuss strategy.

To get this up and running manually…

 - clone this repository,
 - build the project and
 - execute it with the main class `de.bripkens.ha.App` and the path to the
   config file as its only argument.

## Configuration
In order to make use of the health check adapter, you will need to configure
the components and reporters. The configuration is done in YAML format. The
following example shows a simple configuration. For further reference,
inspect the [class definitions](https://github.com/bripkens/health-check-adapter/blob/master/src/main/scala/de/bripkens/ha/Configuration.scala)
into which the configuration will be deserialized.

```yaml
---
endpoints:
  - url: http://127.0.0.1:8081/healthcheck
    id: shopping
    name: Shopping System
    # How often the health of the component should be checked in millis
    interval: 3000
    reporter: mySlackReporter
  - url: http://127.0.0.1:8181/healthcheck
    id: recommendation
    name: Recommendation system
    interval: 10000
    reporter: myConsoleReporter

reporters:
  mySlackReporter:
    type: slack
    # either use a channel (identified via leading #) or
    # send a direct message to a user (identified via leading @)
    channel: #test
    # see the Slack WebHook API reference to generate this url:
    # https://api.slack.com/incoming-webhooks
    webhookUrl: https://hooks.slack.com/services/<some secret id>
    botName: Health Check
    botImage: http://lorempixel.com/64/64/
  myConsoleReporter:
    type: console
```

## FAQ

- **We are not using Slack, can we use this adapter with X?**: This health
  check adapter has a concept of reporters. Currently only a Slack and console
  reporter exist. Feel free to open a pull request to add another reporting
  integration!

## Releasing a new Docker image
Make sure that you are signed in to Docker Hub via `docker login` and then
execute the `./build-docker` script. The script will build the project, build
the new Docker image and push it to Docker Hub.

## License (MIT)
The MIT License (MIT)

Copyright (c) 2015 Ben Ripkens

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

