# Java 14 New Features

This project covers the Java 14 new features. Java 14 is planned to be generally available in March 2020. At this moment I cover only the syntactical features, but by the GA date I plan to cover all the list available at [https://openjdk.java.net/projects/jdk/14/](https://openjdk.java.net/projects/jdk/14/).

The code is available in this project and the explanation and commentary is available in a set of youtube videos. Follow the links below for details.

## Introduction
If you are in the inpatient mode, just start browsing the code. If you have trouble understanding the structure, then maybe it would be good to  spend several minutes on the [introductory video :tv:](https://youtu.be/IUqFQy4yUAw) (it covers the agenda, logistics, code structuring, presentation style etc.).

If you plan to binge-watch the series, I created a [playlist :movie_camera:](https://www.youtube.com/playlist?list=PLGDP1Irs2PmWNwAwMPdyOxCqkFqB6gtp9).

## Syntactical Features

|JEP #|JEP Name|Status|Video Link|Source Link
| --- | ------ | --- | :----: | :--: |
| 361 | Switch Expressions| Standard | [:tv:](https://youtu.be/rSGbMqX5RzU)| [:scroll:](./src/main/java/com/github/kbnt/java14/se/SwitchExpressions.java)
| 368| Text Blocks | Second Preview| [:tv:](https://youtu.be/QU9pQGCVrPY)| [:scroll:](./src/main/java/com/github/kbnt/java14/tb/TextBlocks.java)
| 305| Pattern Matching for _instanceof_ | Preview| [:tv:](https://youtu.be/SJmGyzLayJc)| [:scroll:](./src/main/java/com/github/kbnt/java14/pm/PatternMatchingForInstanceof.java)
| 359| Records | Preview| [:tv:](https://youtu.be/zA11PetGZuk)| [:scroll:](./src/main/java/com/github/kbnt/java14/records/Records.java)

## DevOps Utilities
|JEP #|JEP Name|Status|Video Link|Source Link
| --- | ------ | --- | :----: | :--: |
| 358 | Helpful NullPointerExceptions| Implemented | [:tv:](https://youtu.be/SdzzWN_DyIA)| [:scroll:](./src/main/java/com/github/kbnt/java14/npe/HelpfulNPEMessages.java)

## New APIs
|JEP #|JEP Name|Status|Video Link|Source Link
| --- | ------ | --- | :----: | :--: |
<<<<<<< HEAD
| 370 | Foreign-Memory Access API | Incubator | [:tv:](https://youtu.be/NwXzT8T6mb8)| [:scroll:](./src/main/java/com/github/kbnt/java14/fma/)
=======
| 370 | Foreign-Memory Access API | Incubator | [:tv:](https://youtu.be/NwXzT8T6mb8)| [:scroll:](./src/main/java/com/github/kbnt/java/14/fma/)
>>>>>>> branch 'master' of git@github.com:knowledge-base-and-tutorials/java14-features.git

### Stay Tuned :wink: !

## Other resources addressing the same topic
While working on this material, I discovered many other good sources (different than the official [OpenJDK](https://openjdk.java.net/projects/jdk/14/) site) covering the new Java features. I decided to promote the ones that I liked, especially that they address the topic in a different format than the one I use:

* [FOSDEM'20](https://fosdem.org/2020/schedule/track/free_java/) has a lot of talks and I can enumerate a few that I personally saw:
  * [State of OpenJDK](https://fosdem.org/2020/schedule/event/state_openjdk/) by Mark Reinhold, the Chief  Architect of the Java Platform Group at Oracle.
  * [ByteBuffers are dead, long live ByteBuffers!](https://fosdem.org/2020/schedule/event/bytebuffers/), a talk about [JEP 370](https://openjdk.java.net/jeps/370), by the owner of that JEP, 	Maurizio Cimadamore.
  * [Helpful NullPointerExceptions - The little thing that became a JEP](https://fosdem.org/2020/schedule/event/npes/) by Christoph Langer, engineer in SAP's Java VM team.
* [Vividcode.io](https://vividcode.io/jdk-14-new-features/) did a good job writing about the features and also providing code on [GitHub](https://github.com/VividcodeIO/jdk14-features).
* [Infoq.com](https://www.infoq.com/news/2019/12/java14-feature-freeze/) did a summarization, but there is no deepdive there nor code available.
* [Aboullaite.me](https://aboullaite.me/) addresses some of the new topics (the format is blogging).
