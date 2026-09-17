# Android DTE Management App

Android application designed for the management and processing of electronic documents (DTE), integrating a mobile interface with backend REST APIs.

## Overview

This project provides an Android-based workflow for querying, managing and processing electronic documents through backend services.

The application is designed around business-oriented document workflows, combining mobile UI, REST API communication and local data management.

## Features

* Electronic document (DTE) management
* REST API integration
* Document querying
* Structured JSON data processing
* Local data persistence
* Business document workflows
* Authentication for backend communication
* Android Material-based user interface

## Technologies

* Java
* Android Studio
* Android SDK
* Retrofit
* REST APIs
* JSON
* Room
* SDK UROVO
* Material Design

## Architecture

The application separates the main responsibilities of the mobile solution into different layers:

```text
Android UI
    │
    ▼
Application / Business Logic
    │
    ├───────────────┐
    ▼               ▼
REST API         Local Database
    │               │
    ▼               ▼
Backend            Room 
```

This structure allows the application to communicate with backend services while maintaining local application data when required.

## API Integration

The application communicates with backend services through HTTP/REST endpoints.

Typical workflow:

```text
User
 │
 ▼
Android Application
 │
 ▼
REST API
 │
 ▼
Backend Service
 │
 ▼
Database / Business Process
```

Sensitive production endpoints, credentials and authentication secrets are intentionally excluded from the repository.


