package com.grading.service;

import com.grading.model.Student;

/**
 * STAGE 2 - Conditional Evaluation.
 *
 * Computes each student's weighted average and converts it into a letter
 * grade using a multi-tier if / else-if threshold ladder.
 */
public class GradeEvaluator {

	// Component weights (must add up to 1.00)
	public static final double QUIZ_WEIGHT = 0.20;
	public static final double ASSIGNMENT_WEIGHT = 0.20;
	public static final double MIDTERM_WEIGHT = 0.25;
	public static final double FINAL_WEIGHT = 0.35;

	// Every letter grade from highest to lowest (used for reports)
	public static final String[] LETTER_GRADES = { "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F" };

	public static void evaluate(Student s) {
		double average = s.getQuiz() * QUIZ_WEIGHT
				+ s.getAssignment() * ASSIGNMENT_WEIGHT
				+ s.getMidterm() * MIDTERM_WEIGHT
				+ s.getFinalExam() * FINAL_WEIGHT;

		// round to 2 decimal places
		average = Math.round(average * 100.0) / 100.0;

		s.setAverage(average);
		s.setLetterGrade(toLetterGrade(average));
	}

	/** Multi-tier thresholds: the first range the average reaches decides the letter. */
	public static String toLetterGrade(double average) {
		String letter;

		if (average >= 97) {
			letter = "A+";
		} else if (average >= 93) {
			letter = "A";
		} else if (average >= 90) {
			letter = "A-";
		} else if (average >= 87) {
			letter = "B+";
		} else if (average >= 83) {
			letter = "B";
		} else if (average >= 80) {
			letter = "B-";
		} else if (average >= 77) {
			letter = "C+";
		} else if (average >= 73) {
			letter = "C";
		} else if (average >= 70) {
			letter = "C-";
		} else if (average >= 60) {
			letter = "D";
		} else {
			letter = "F";
		}

		return letter;
	}
}
