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
        <li><a href="#project-structure">Project Structure</a></li>
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
<li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>


<!-- ABOUT THE PROJECT -->
## About The Project

This project is the implementation of an anonymous chat that runs over a P2P network, built through the support of the framework/library [TomP2P](https://tomp2p.net/), for the ADC class at the [Università degli Studi di Salerno](https://www.unisa.it/).

The main requirement for this project were asynchronous and anonymous communication. We were able to achieve those requirements through the usage of the **publish/subscribe paradigm**, a well known paradigm in the asynchronous communication literature.



Further information regarding this assignment can be found at the [ADC class page](https://spagnuolocarmine.github.io/adc.html).

<p align="right">(<a href="#top">back to top</a>)</p>


### Project structure

TODO
TODO
TODO



### Built With

* [Maven](https://maven.apache.org/)
* [TomP2P](https://tomp2p.net/)
* [Docker](https://www.docker.com/)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

**Docker** must be installed on your machine in order to run this project.
Check the official docker [get-started page](https://docs.docker.com/get-started/) for more information.

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

## Acknowledgments

Additional libraries used:
TODO
TODO
TODO


<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/Guilty994/liguori_domenico_adc_2021/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/domenico-liguori-1435a8215/