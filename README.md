# AdvancedProg – MiniHTTP + Reactive Graph Implementation

The code was produced as part of the Advanced Programming course (BIU) and is dependency-free, so you can copy-paste the **entire `AdvancedProg/` folder** (sources, HTML templates, sample config files and generated docs) into any Java 8+ project or package it as a JAR.

---
## Javadoc

In the project folder go to `docs` folder.
Open `index.html`.

Or in the IDE drag `index.html` to the browser.

---
## Background
This project simulates a network of **agents** that exchange messages via **topics**.

### Core concepts
| Term | What it is | Example |
|------|------------|---------|
| **Topic** | Named channel that stores the *latest* {@link graph.Message}. Agents can subscribe&nbsp;(`topic.subscribe(agent)`) or publish (`topic.publish(msg)`). | `A`, `B`, `C` |
| **Message** | Immutable payload (text, bytes and lazy-double). | `"42"`, `"18.5"` |
| **Agent** | Node that reacts to incoming messages and usually publishes a result to another topic. | `IncAgent` adds 1, `PlusAgent` sums two topics, `ParallelAgent` wraps any agent in its own thread |

Agents and Topics form a directed graph: when a topic receives a new message all its subscriber agents are invoked, who may produce further messages, and so on.

At runtime the system:
1. Parses a plain-text configuration file that describes which Java `Agent` classes to load and how they connect to topics.
2. Creates each agent in its own thread (via `ParallelAgent`) so computations run concurrently.
3. Exposes a minimal **HTTP server** (default port `8080`) with a browser UI:
   * left pane – upload a new configuration or publish a single message to a topic.
   * centre pane – live graph visualisation; each topic node shows its latest value.
   * right pane – table of every topic and its last message.

---
## Quick Start
```java
// Create a server listening on port 8080 (5 worker threads)
HTTPServer server = new MyHTTPServer(8080, 5);

// Serve static pages from html_files/
server.addServlet("GET", "/app/", new HtmlLoader("html_files"));

// Expose live graph state as JSON
server.addServlet("GET", "/topics", new TopicsJsonServlet());

// Start (non-blocking)
server.start();
```
Visit `http://localhost:8080/app/index.html`.

Shutdown gracefully:
```java
server.close();
```

### Quick Start 
If you prefer to see the full browser UI immediately, just run `Main.java` from your IDE:

```text
Open http://localhost:8080/app/index.html in your browser.
```

---
## Folder Structure

```text
AdvancedProg/
│  README.md               ← (you are here)
│  .gitignore
├─ project_biu/            ← Java sources
│   ├─ configs/            ← Graph builders & config loader
│   ├─ graph/              ← Topic, Agent & messaging runtime
│   ├─ server/             ← HTTP server + request parser
│   ├─ servlets/           ← Handlers that serve the UI & JSON
│   ├─ views/              ← HtmlGraphWriter
│   └─ Main.java           ← Entry-point (launches playground)
├─ html_files/             ← Static resources for the browser UI
|─ docs/                   ← Documentation 
└─ config_files/           ← Example `.conf` files
```

---
## Configuration File Format

Each **agent** is described by *three consecutive non-empty lines* in a `.conf` file:

1. **Class name** – fully-qualified Java class that implements `graph.Agent`.
2. **Subscribed topics** – comma-separated list of topic names the agent reads from.
3. **Published topics** – comma-separated list of topic names the agent writes to.

Example with two agents:

```conf
# PlusAgent adds two inputs and publishes the result to C
graph.PlusAgent
A,B
C

# Increment C and publish to D
graph.IncAgent
C
D
```

Lines may contain spaces around the commas; blank lines are ignored.
Upload the file via the *Upload Configuration* form in the UI.

---
## Reactive Graph Snippet
```java
Topic src = TopicManagerSingleton.get().getOrCreate("T1");
Topic dst = TopicManagerSingleton.get().getOrCreate("T2");
Agent inc = new IncAgent("A_inc", 1, src, dst);

src.publish(new Message("41")); 
```
Generate an HTML view:
```java
String html = HtmlGraphWriter.getGraphHTML(Graph.builder().withNodes(src, dst, inc).build()).get(0);
Files.write(Paths.get("graph.html"), html.getBytes(StandardCharsets.UTF_8));
```

---
## Usage Walkthrough
1. **Upload a configuration** (left pane) – choose any file under `config_files/` or craft your own.
2. The graph diagram appears.  Topic nodes show an empty value until something is published.
3. **Publish a value** via the second form.  The table on the right updates and the graph refreshes.
4. Uploading a new config clears previous state and redraws a fresh graph.

---
## Requirements
* Java 8+
* No external dependencies

---
