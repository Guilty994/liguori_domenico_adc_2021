<div id="top"></div>


<!-- PROJECT SHIELDS -->
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/LOGO.png" alt="Logo" width="80" height="80">
  </a>

<h2 align="center">Anonymous Chat</h2>
  <h4>Student: </h4> <code>Domenico Liguori</code>
  <h4>MD5(domenicoliguori-07)</h4> <code>f298d34696d8dd673915a1bb95dfd097</code>
</div>



<!-- TABLE OF CONTENTS -->

  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About this project</a>
      <ul>
        <li><a href="#proposed-solution">Proposed solution</a>
            <ul>
                <li><a href="#join">Join</a>
                <li><a href="#interact">Interact</a>
                <li><a href="#leave">Leave</a>
            </ul>
        </li>
        <li><a href="#project-structure">Project Structure</a></li>
        <li><a href="#junit-tests">JUnit tests</a></li>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
  </ol>



<!-- ABOUT THE PROJECT -->
## About The Project

This project is the implementation of an anonymous chat that runs over a P2P network based on the [example project](https://github.com/spagnuolocarmine/p2ppublishsubscribe) by [Carmine Spagnuolo](https://github.com/spagnuolocarmine) for the ADC class at the [Università degli Studi di Salerno](https://www.unisa.it/).

The main requirement for this work were asynchronous and anonymous communication. We were able to achieve those requirements through the usage of the [publish/subscribe paradigm](https://www.pubnub.com/learn/glossary/what-is-publish-subscribe/), a well known paradigm in the asynchronous communication literature, and the framework/library [TomP2P](https://tomp2p.net/).

Further information regarding this assignment can be found at the [ADC class page](https://spagnuolocarmine.github.io/adc.html).

<p align="right">(<a href="#top">back to top</a>)</p>

### Proposed solution

The lifecycle for each peer has been divided in 3 different stages: **Join**, **Interact** and **Leave**.

#### Join

TODO

#### Interact

TODO

#### Leave

TODO

<p align="right">(<a href="#top">back to top</a>)</p>

### Project structure

The package `it.adc.p2p.chat` provides 5 Java **Classes** and 1 Java **Interface**:

* `AnonymousChat` **Interface** that define the publish/subscribe paradigm.
* `AnonymousChatImpl` **Class** that implements the _AnonymousChat_ interface, exploiting TomP2P library and providing a basic API for anonymous chats. 
* `Heartbeat` **Class** used to implement an asynchronous task that periodically spot crashed peers in the network.
* `MessageListener` **Class** that implements a listener used by the peers to listen for incoming messages.
* `ShutDownProcedure` **Class** that define the operations to perform once the JVM exit or is terminated.
* `StartChat` **Class** that use the provided API to start an example anonymous chat.

The package `it.adc.p2p.chat.exceptions` provides 4 Java **Exception**:

* `DNSException` **Exception** triggered when something went wrong during the peers DNS update/creation.
* `DuplicatePeer` **Exception** triggered when a peer try to join the network with an ID another peer already claimed.
* `FailedMasterPeerBootstrap` **Exception** triggered when an error occur during the bootstrap to the master peer.
* `NetworkError` **Exception** triggered when a peer wasn't able to contact the network.

<p align="right">(<a href="#top">back to top</a>)</p>

### JUnit tests

A series of unit tests have been provided in the test package `it.adc.p2p.chat` to check out all the API functionalities.

* `TestAnonymousChat::testCase_DuplicatePeers()` **Test** that checks the API behavior when there is multiple peers with same ID.
* `TestAnonymousChat::testCase_MasterBootstrapping()` **Test** that checks different behavior of the API when a correct or incorrect bootstrap to the p2p network happen.
* `TestAnonymousChat::testCase_CreateNonExistingRoom()` **Test** that checks the API behavior when a peer try to create a new room.
* `TestAnonymousChat::testCase_CreateExistingRoom()` **Test** that checks the API behavior when a peer try to create a room that already exist.
* `TestAnonymousChat::testCase_JoinRoom()` **Test** that checks the API behavior when a peer try to join a room that exist and a room that doesn't exist.
* `TestAnonymousChat::testCase_SendMessage()` **Test** that checks the API behavior when a correct or incorrect message sending is performed.
* `TestAnonymousChat::testCase_LeaveRoom()` **Test** that checks the API behavior when a peer try to leave a room he already joined, a room he never joined or a room that doesn't exist.

<p align="right">(<a href="#top">back to top</a>)</p>

### Built With

* [Maven](https://maven.apache.org/) 3.5.1
* [JUnit](https://junit.org/) 4.12
* [TomP2P](https://tomp2p.net/) 5.0-Beta8
* [Docker](https://www.docker.com/) 

For a better overview of all the plugins and libraries used, check out the `pom.xml` in the project root.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

This project provides a simple example of an anonymous chat that uses the provided API. To start this example follow the steps below.

<p align="right">(<a href="#top">back to top</a>)</p>

### Prerequisites

**Docker** must be installed on your machine in order to run this project.
Check the official docker [get-started page](https://docs.docker.com/get-started/) for more information.

<p align="right">(<a href="#top">back to top</a>)</p>

### Installation

1. Download the Dockerfile
   ```
   curl -OL https://raw.githubusercontent.com/Guilty994/liguori_domenico_adc_2021/master/Dockerfile
   ```
2. Build the Docker image
   ```
   docker build --no-cache -t anonchat .
   ```

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- USAGE EXAMPLES -->
## Usage

<!-- docker run --name PEER_1 -e ID=1 -e MASTERIP="172.17.0.2" -it anonchat -->

1. Start a new master peer
   ```
   docker run --name MASTER -e ID=0 -e MASTERIP="127.0.0.1" -it anonchat
   ```
2. Check the _@container_address_
   ```
   docker ps
   docker inspect <container ID>
   ```
3. Start a new peers using the _@container_address_ as MASTERID and varying the unique _@peerid_ ∈ [1, +∞]
    ```
   docker run --name PEER_[peer id] -e ID=@peerid -e MASTERIP="@container_address" -it anonchat
   ```
<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/Guilty994/liguori_domenico_adc_2021/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/domenico-liguori-1435a8215/