package com.grading.service;

import com.grading.model.Student;

/**
 * STAGE 2 - Conditional Evaluation.
 *
 * Computes each student's weighted average and converts it into the
 * school's grade (1.00 - 5.00) and letter grade using a multi-tier
 * if / else-if threshold ladder.
 */
public class GradeEvaluator {

	// Component weights (must add up to 1.00)
	public static final double QUIZ_WEIGHT = 0.20;
	public static final double ASSIGNMENT_WEIGHT = 0.20;
	public static final double MIDTERM_WEIGHT = 0.25;
	public static final double FINAL_WEIGHT = 0.35;

	// Lowest passing average (3.00 / C-)
	public static final double PASSING_AVERAGE = 75;

	// The school's grading system, from highest to lowest (same order in all three)
	public static final String[] LETTER_GRADES = { "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F" };
	public static final String[] NUMERIC_GRADES = { "1.00", "1.25", "1.50", "1.75", "2.00", "2.25", "2.50", "2.75", "3.00", "4.00", "5.00" };
	public static final String[] PERCENT_RANGES = { "98 - 100", "95 - 97", "92 - 94", "89 - 91", "86 - 88", "83 - 85",
			"80 - 82", "77 - 79", "75 - 76", "70 - 74", "Below 70" };

	public static void evaluate(Student s) {
		double average = s.getQuiz() * QUIZ_WEIGHT
				+ s.getAssignment() * ASSIGNMENT_WEIGHT
				+ s.getMidterm() * MIDTERM_WEIGHT
				+ s.getFinalExam() * FINAL_WEIGHT;

		// round to 2 decimal places
		average = Math.round(average * 100.0) / 100.0;

		String letter = toLetterGrade(average);
		s.setAverage(average);
		s.setLetterGrade(letter);
		s.setNumericGrade(toNumericGrade(letter));
	}

	/** Multi-tier thresholds: the first range the average reaches decides the letter. */
	public static String toLetterGrade(double average) {
		String letter;

		if (average >= 98) {
			letter = "A+";
		} else if (average >= 95) {
			letter = "A";
		} else if (average >= 92) {
			letter = "A-";
		} else if (average >= 89) {
			letter = "B+";
		} else if (average >= 86) {
			letter = "B";
		} else if (average >= 83) {
			letter = "B-";
		} else if (average >= 80) {
			letter = "C+";
		} else if (average >= 77) {
			letter = "C";
		} else if (average >= 75) {
			letter = "C-";
		} else if (average >= 70) {
			letter = "D";
		} else {
			letter = "F";
		}

		return letter;
	}

	/** The school's numeric grade for a letter, e.g. "A" -> "1.25". */
	public static String toNumericGrade(String letter) {
		for (int i = 0; i < LETTER_GRADES.length; i++) {
			if (LETTER_GRADES[i].equals(letter)) {
				return NUMERIC_GRADES[i];
			}
		}
		return "";
	}
}
