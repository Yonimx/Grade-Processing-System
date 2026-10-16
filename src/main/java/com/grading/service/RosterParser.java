package com.grading.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grading.model.Student;

/**
 * STAGE 1 - Iterative Batch Processing.
 *
 * Reads a whole class roster (CSV text) one line at a time and turns every
 * valid line into a Student object. Invalid lines are skipped and a message
 * explaining why is added to the errors list.
 *
 * Expected line format:
 *   Student ID, Name, Quiz, Assignment, Midterm, Final Exam
 */
public class RosterParser {

	private static final String[] COMPONENTS = { "Quiz", "Assignment", "Midterm", "Final Exam" };

	public static List<Student> parse(String rosterText, List<String> errors) {
		List<Student> students = new ArrayList<>();
		Set<String> usedIds = new HashSet<>();

		if (rosterText == null || rosterText.isBlank()) {
			return students;
		}

		// Files saved by Excel as "CSV UTF-8" start with an invisible byte-order mark
		if (rosterText.startsWith("﻿")) {
			rosterText = rosterText.substring(1);
		}

		String[] lines = rosterText.split("\\r?\\n");
		boolean firstRecord = true;

		for (int i = 0; i < lines.length; i++) {
			int lineNo = i + 1;
			String line = lines[i].trim();

			// skip blank lines and comment lines
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			String[] parts = line.split(",");
			if (parts.length < 6) {
				errors.add("Line " + lineNo + ": expected 6 values (ID, Name, Quiz, Assignment, Midterm, Final) but found "
						+ parts.length + ".");
				firstRecord = false;
				continue;
			}

			String id = clean(parts[0]);

			// The name may contain commas (e.g. "Dela Cruz, Juan"), so everything
			// between the ID and the last four scores is treated as the name.
			StringBuilder nameBuilder = new StringBuilder();
			for (int j = 1; j < parts.length - 4; j++) {
				if (j > 1) {
					nameBuilder.append(",");
				}
				nameBuilder.append(parts[j]);
			}
			String name = clean(nameBuilder.toString());

			// read the four scores
			String[] tokens = new String[4];
			double[] scores = new double[4];
			String badValue = null;
			int badCount = 0;
			for (int k = 0; k < 4; k++) {
				tokens[k] = clean(parts[parts.length - 4 + k]);
				try {
					scores[k] = Double.parseDouble(tokens[k]);
				} catch (NumberFormatException e) {
					badCount++;
					if (badValue == null) {
						badValue = COMPONENTS[k] + " value \"" + tokens[k] + "\" is not a number";
					}
				}
			}

			// a first line with no numbers at all is the column header, so skip it quietly
			boolean isHeader = firstRecord && badCount == 4;
			firstRecord = false;
			if (isHeader) {
				continue;
			}
			if (badValue != null) {
				errors.add("Line " + lineNo + ": " + badValue + ".");
				continue;
			}

			if (id.isEmpty() || name.isEmpty()) {
				errors.add("Line " + lineNo + ": student ID and name are required.");
				continue;
			}

			String rangeError = null;
			for (int k = 0; k < 4; k++) {
				if (Double.isNaN(scores[k]) || scores[k] < 0 || scores[k] > 100) {
					rangeError = COMPONENTS[k] + " score " + tokens[k] + " must be between 0 and 100";
					break;
				}
			}
			if (rangeError != null) {
				errors.add("Line " + lineNo + " (" + name + "): " + rangeError + ".");
				continue;
			}

			if (!usedIds.add(id.toUpperCase())) {
				errors.add("Line " + lineNo + " (" + name + "): duplicate student ID " + id + ", record skipped.");
				continue;
			}

			students.add(new Student(id, name, scores[0], scores[1], scores[2], scores[3]));
		}

		return students;
	}

	/** Trims a value and removes surrounding double quotes added by spreadsheet programs. */
	private static String clean(String value) {
		String v = value.trim();
		if (v.length() >= 2 && v.startsWith("\"") && v.endsWith("\"")) {
			v = v.substring(1, v.length() - 1).trim();
		}
		return v;
	}
}
