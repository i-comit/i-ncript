<a name="readme-top"></a>

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="/build/appicon.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">i-ncript</h3>

  <p align="center">
    A portable, cross-platform data encryption app.
    <br />

  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
        <li><a href="#common-issues">Common Issues</a></li>
      </ul>
    </li>
    <li>
    <ul>
      </ul>
      <a href="#getting-started">Getting Started</a>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## Introduction

[![Product Name Screen Shot][product-screenshot]](https://example.com)
<div style="display: flex; justify-content: space-around; align-items: center; width: 100%;">

<img src="https://example.com/image1.jpg" alt="Image 1" style="flex-grow: 1; max-width: 33%; height: auto;">
<img src="https://example.com/image2.jpg" alt="Image 2" style="flex-grow: 1; max-width: 33%; height: auto;">
<img src="https://example.com/image3.jpg" alt="Image 3" style="flex-grow: 1; max-width: 33%; height: auto;">

</div>

i-ncript is a portable data encryption app, designed for use with removable storage devices. It is portable as it does not require aninstaller to run, can execute on any drive, and can be quickly transferred to another device without an install/uninstall process.

The focus on portability plays an important role in data security. The fact is, data is most vulnerable is when it is connected to the internet, and while many measures have been taken to ensure the safety of online data, an offline, portable data store is a far more simple yet far more effective method of data security.

Files are encrypted using the Advanced Encryption Standard (AES), which employs a sophisticated round-based encryption method to provide a robust security foundation. Your key is additionally encrypted using the Argon2i algorithm, which enhances security by resisting side-channel attacks and using "salting" to defend against rainbow tables.

You can also send 

Summary:

* Lightweight (<4MB) self contained executable.
* Encryption using AES & Argon2i.
* Send & receive files with one time password.
* Cross platform for desktop OS, and soon mobile.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Wails][Wails_Badge]][Wails-url]
* [![Svelte][Svelte_Badge]][Svelte-url]
* [![Tailwind][Tailwind_Badge]][Tailwind-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Common Errors

> On Linux, I got an "Invalid url:path/to/app No ':' in the uri" error and can't open the app in my USB
>>This is a permission issue. Make sure the USB is formatted with NTFS.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

To compile this app. You will need to install [![Node.js][Node.js_Badge]][Node.js-url] &  [![Go][Golang_Badge]][Golang-url]

#### 1. first install the go modules

  ```sh
  go mod download
  go mod tidy
  ```

* optionally check for updates

  ```sh
  go list -u -m all
  ```

#### 2. then cd to /frontend

  ```sh
  cd frontend
  ```

* and update/install npm

  ```sh
  npm install npm@latest -g
  npm install
  ```

#### 3. to develop the app live, use

  ```sh
  wails dev
  ```

#### 4. to build a distributable, use

  ```sh
  wails build
  ```

<!-- ROADMAP -->
## Roadmap

You can track the current project roadmap <a href="https://github.com/users/khiemgluong/projects/3">here</a>:
See the [open issues](https://github.com/i-comit/i-ncript/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distributed under the GNU GPL v3 License. See the <a href="./LICENSE">LICENSE</a> for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

<khiemluong@i-comit.com>

[Source Forge](https://sourceforge.net/projects/i-ncript/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[product-screenshot]: images/screenshot.png

[Wails_Badge]:https://img.shields.io/badge/Wails-red?style=for-the-badge&logo=Wails
[Wails-url]: https://wails.io
[Svelte_Badge]: https://img.shields.io/badge/Svelte-4A4A55?style=for-the-badge&logo=svelte&logoColor=FF3E00
[Svelte-url]: https://svelte.dev/
[Tailwind_Badge]: https://img.shields.io/badge/Tailwind-blue?style=for-the-badge&logo=tailwindcss
[Tailwind-url]: https://tailwindcss.com

[Node.js_Badge]: https://img.shields.io/badge/node.js-green?style=flat&logo=nodedotjs
[Node.js-url]: https://nodejs.org/en
[Golang_Badge]: https://img.shields.io/badge/golang-%231BDBDB?style=flat&logo=go
[Golang-url]: https://go.dev
