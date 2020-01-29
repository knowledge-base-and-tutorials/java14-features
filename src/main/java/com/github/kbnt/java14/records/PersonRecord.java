package com.github.kbnt.java14.records;

/**
 * A very simple record.
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
record PersonRecord(String firstName, String lastName, int age) implements Person {};