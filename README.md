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
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About this project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
        <li><a href="#proposed-solution">Proposed solution</a></li>
        <li><a href="#project-structure">Project Structure</a></li>
        <li><a href="#junit-tests">JUnit tests</a></li>
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
</details>


<!-- ABOUT THE PROJECT -->
## About The Project

This project is the implementation of an anonymous chat that runs over a P2P network, built through the support of the framework/library [TomP2P](https://tomp2p.net/), for the ADC class at the [Università degli Studi di Salerno](https://www.unisa.it/).

The main requirement for this project were asynchronous and anonymous communication. We were able to achieve those requirements through the usage of the [publish/subscribe paradigm](https://www.pubnub.com/learn/glossary/what-is-publish-subscribe/), a well known paradigm in the asynchronous communication literature.



Further information regarding this assignment can be found at the [ADC class page](https://spagnuolocarmine.github.io/adc.html).

<p align="right">(<a href="#top">back to top</a>)</p>


### Proposed solution

TODOTODOTODOTODOTODOTODOTODO

<p align="right">(<a href="#top">back to top</a>)</p>

### Project structure

The package `it.adc.p2p.chat` provides 3 Java **Classes** and 1 Java **Interface**:

* <**I**> _AnonymousChat_ Interface that define the publish/subscribe paradigm.
* <**C**> _AnonymousChatImpl_ Implementation of _AnonymousChat_ that exploits TomP2P library, providing a basic API for anonymous chats. 
* <**C**> _MessageListener_ The listener that is used by the peers to listen for incoming messages.
* <**C**> _StartChat_ A class that use the provided API to start an example anonymous chat.

The package `it.adc.p2p.chat.Exceptions` provides 1 Java **Exception**:

* <**E**> _FailedMasterPeerBootstrap_ This exception is triggered when an error occur during the bootstrap to the master peer.

<p align="right">(<a href="#top">back to top</a>)</p>

### JUnit tests

A series of unit tests have been provided in the test package `it.adc.p2p.chat` to check out all the API functionalities.

* _TestAnonymousChat::testCase_MasterBootstrapping_ TODOTODOTODOTODOTODOTODO
* _TestAnonymousChat::testCase_CreateNonExistingRoom_
* _TestAnonymousChat::testCase_CreateExistingRoom_
* _TestAnonymousChat::testCase_JoinRoom_
* _TestAnonymousChat::testCase_SendMessage_
* _TestAnonymousChat::testCase_LeaveRoom_

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

### Prerequisites

**Docker** must be installed on your machine in order to run this project.
Check the official docker [get-started page](https://docs.docker.com/get-started/) for more information.

<p align="right">(<a href="#top">back to top</a>)</p>

### Installation

1. Clone the repo locally on your machine
   ```
   git clone https://github.com/Guilty994/liguori_domenico_adc_2021.git
   ```
2. To build the Docker image run
   ```
   docker build --no-cache -t anonchat .
   ```

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- USAGE EXAMPLES -->
## Usage

1. Once the image is built, start a new master peer
   ```
   docker run --name MASTER -e ID=0 -e MASTERIP="127.0.0.1" -it anonchat
   ```
2. Check the _@container_address_
   ```
   docker ps
   docker inspect <container ID>
   ```
3. Now you can start a new peers using the _@container_address_ as MASTERID and varying the unique _@peerid_
    ```
   docker run --name PEER_[peer id] -e ID=@peerid -e MASTERIP="@container_address" -it anonchat
   ```
<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/Guilty994/liguori_domenico_adc_2021/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/domenico-liguori-1435a8215/