# LTSA — Labelled Transition System Analyser

A verification tool for concurrent systems, with a working MSC (Message Sequence Chart) plugin.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
  - [Modelling](#modelling)
  - [Verification](#verification)
  - [Visualisation and Animation](#visualisation-and-animation)
  - [Files](#files)
- [Plugins](#plugins)
- [Requirements](#requirements)
- [Download](#download)
- [Running LTSA](#running-ltsa)
  - [Windows](#windows)
  - [Linux](#linux)
  - [macOS](#macos)
  - [Hiding Console Messages](#hiding-console-messages)
- [Getting Started](#getting-started)
- [Manual and Examples](#manual-and-examples)
- [License](#license)

## Overview

LTSA (Labelled Transition System Analyser) is a verification tool for concurrent systems. It automatically checks whether the specification of a concurrent system satisfies its required behavioural properties, and supports specification animation for interactive exploration of how a system behaves.

Models are written in **FSP** (Finite State Processes), a concise textual notation. LTSA compiles each FSP description into an LTS (Labelled Transition System), which it can then verify, visualise, and animate.

The MSC (Message Sequence Chart) plugin extends LTSA with the ability to graphically edit sets of scenarios and construct message sequence charts, offering a visual way to explain models. As part of an iterative design process, LTSA can also help surface implied behaviour within a system.

This repository contains an extended version of LTSA with a working MSC plugin, updated to run on modern systems.

> **Note:** The [official LTSA release](https://www.doc.ic.ac.uk/ltsa) was last updated in June 2006, and its additional plug-ins are no longer functional.

## Features

### Modelling

- Built-in editor for FSP specifications, with cut, copy, paste, undo, and redo
- Parse, compile, and compose processes into a single target system
- Minimise a composed system to its smallest equivalent form

### Verification

- **Safety** — detects deadlocks and property violations
- **Progress** — checks liveness, confirming desired actions eventually occur
- **LTL (Linear Temporal Logic)** — verifies specifications against temporal logic properties
- **Supertrace** — explores very large state spaces using bit-state hashing, with configurable hashtable size and search depth
- Reduction options including POR (Partial Order Reduction) and tau reduction
- Failed checks produce an error trace that can be replayed step by step

### Visualisation and Animation

- Draw the LTS as a state machine diagram
- Inspect a process alphabet and its transitions
- Animate a specification interactively, including SceneBeans graphical animation
- Build and edit message sequence charts with the MSC editor

### Files

- Save and open FSP models as `.lts` files
- Export a target composition in Aldebaran (`.aut`) format
- Print diagrams and output

## Plugins

LTSA includes the following plugins. See the [official documentation](https://www.doc.ic.ac.uk/ltsa) for full details on each.

- **MSC (Message Sequence Chart)** — editor described in the [Overview](#overview) above
- **Scenebeans** — graphical animation of specifications
- **Web** — web-based animation support

## Requirements

**Java 8 or newer.** Nothing else needs to be installed.

Check whether you already have it:

```bash
java -version
```

If Java is missing, download it from the [official Java website](https://www.java.com/download/).

## Download

Get the latest release from the [Releases page](../../releases), then extract the archive.

The archive contains `ltsa.jar` together with the `plugins/` folder. Keep them side by side — LTSA loads plugins from a `plugins/` folder next to where you launch it.

## Running LTSA

The same command works on every platform:

```bash
cd /path/to/ltsa
java -jar ltsa.jar
```

> **Important:** Always start LTSA from the folder that contains `plugins/`. LTSA looks for that folder relative to your current directory, so launching from anywhere else opens the application without any plugins.

### Windows

Double-click `ltsa.jar`, or run the command above from the extracted folder.

### Linux

Run the command above, or create a desktop entry at `~/.local/share/applications/ltsa.desktop` (replace `YOURUSER`):

```desktop
[Desktop Entry]
Type=Application
Name=LTSA
Comment=Labelled Transition System Analyser
Exec=java -jar /home/YOURUSER/ltsa/ltsa.jar
Path=/home/YOURUSER/ltsa
Icon=applications-science
Terminal=false
Categories=Development;Education;Science;
```

```bash
chmod +x ~/.local/share/applications/ltsa.desktop
```

The `Path=` line sets the working directory so plugins load correctly.

Alternatively, use a small script placed beside the jar:

```bash
#!/bin/bash
cd "$(dirname "$0")"
java -jar ltsa.jar
```

### macOS

Run the command above from the extracted folder.

### Hiding Console Messages

LTSA may print harmless system messages to the console while running. They can be safely ignored, or hidden by discarding that output.

Windows, PowerShell:

```powershell
java -jar ltsa.jar 2>$null
```

Windows, Command Prompt:

```batch
java -jar ltsa.jar 2>nul
```

Linux and macOS:

```bash
java -jar ltsa.jar 2>/dev/null
```

## Getting Started

1. **Write a model** — type an FSP specification in the editor, or open an existing `.lts` file from the **File** menu.
2. **Compile it** — use **Build > Compile** to turn the specification into an LTS. Any errors appear in the output pane.
3. **Compose the system** — use **Build > Compose** to combine processes into the target system you want to analyse.
4. **Verify it** — run **Check > Safety** to look for deadlocks, then **Check > Progress** for liveness. If a check fails, LTSA reports a trace showing exactly how the problem is reached.
5. **Explore the result** — open the **Window** menu to draw the LTS, view the alphabet and transitions, animate the model, or open the MSC editor.

Options such as warning behaviour, reduction settings, and drawing preferences are under the **Options** menu.

## Manual and Examples

The full user manual is bundled with the application:

1. Start LTSA.
2. Open the **Help** menu in the menu bar.
3. Select **Manual**.

The **Help** menu also provides **Examples**, a set of ready-made models useful for learning FSP.

## License

Licensed under the [Apache License 2.0](LICENSE).
