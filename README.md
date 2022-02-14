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
  <h4>Student: </h4> `Domenico Liguori`
  <h4>MD5(domenicoliguori-07)</h4> `f298d34696d8dd673915a1bb95dfd097`
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About this project</a>
      <ul>
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
    <li><a href="#license">License</a></li>
  </ol>
</details>


<!-- ABOUT THE PROJECT -->
## About The Project

This project is the implementation of an anonymous chat that runs over a P2P network, built through the support of the framework/library [TomP2P](https://tomp2p.net/), for the ADC class at the [Università degli Studi di Salerno](https://www.unisa.it/).

Further information regarding this project assignment can be found at the [ADC class page](https://spagnuolocarmine.github.io/adc.html).

<p align="right">(<a href="#top">back to top</a>)</p>

### Built With

* [Maven](https://maven.apache.org/)
* [TomP2P](https://tomp2p.net/)
* [Docker](https://www.docker.com/)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

**Docker** must be installed on your machine, in order to run this project.

### Installation

1. Clone the repo locally on your machine
   ```sh
   git clone https://github.com/Guilty994/liguori_domenico_adc_2021.git
   ```
2. To build the project Docker image run
   ```sh
   docker build --no-cache -t anonchat .
   ```

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- USAGE EXAMPLES -->
## Usage

1. Once the image is built, start a new Master peer
   ```sh
   docker run --name MASTER -e ID=0 -e MASTERIP="127.0.0.1" -it anonchat
   ```
2. Check the container address
     ```sh
   docker ps
   docker inspect <container ID>
   ```
3. Now you can start a new peer using the container address as MASTERID and varying the unique ID for each peer
    ```sh
   docker run --name PEER_[peer id] -e ID=[peer id] -e MASTERIP="[container address]" -it anonchat
   ```
<p align="right">(<a href="#top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/Guilty994/liguori_domenico_adc_2021/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/domenico-liguori-1435a8215/