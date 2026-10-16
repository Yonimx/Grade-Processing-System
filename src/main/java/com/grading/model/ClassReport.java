package com.grading.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class-wide summary produced by ReportGenerator after every student
 * has been evaluated.
 */
public class ClassReport implements Serializable {

	private static final long serialVersionUID = 2L;

	private String courseName;
	private String generatedOn;

	private int totalStudents;
	private double classAverage;
	private double median;

	private double highestAverage;
	private String highestName;
	private double lowestAverage;
	private String lowestName;

	private int passedCount;
	private int failedCount;
	private double passRate;

	// letter grade -> number of students (kept in A+ ... F order)
	private Map<String, Integer> gradeDistribution = new LinkedHashMap<>();
	// academic status -> number of students
	private Map<String, Integer> statusCounts = new LinkedHashMap<>();

	private List<Student> honorRoll = new ArrayList<>();
	private List<Student> remediationList = new ArrayList<>();

	public String getCourseName() { return courseName; }
	public void setCourseName(String courseName) { this.courseName = courseName; }

	public String getGeneratedOn() { return generatedOn; }
	public void setGeneratedOn(String generatedOn) { this.generatedOn = generatedOn; }

	public int getTotalStudents() { return totalStudents; }
	public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

	public double getClassAverage() { return classAverage; }
	public void setClassAverage(double classAverage) { this.classAverage = classAverage; }

	public double getMedian() { return median; }
	public void setMedian(double median) { this.median = median; }

	public double getHighestAverage() { return highestAverage; }
	public void setHighestAverage(double highestAverage) { this.highestAverage = highestAverage; }

	public String getHighestName() { return highestName; }
	public void setHighestName(String highestName) { this.highestName = highestName; }

	public double getLowestAverage() { return lowestAverage; }
	public void setLowestAverage(double lowestAverage) { this.lowestAverage = lowestAverage; }

	public String getLowestName() { return lowestName; }
	public void setLowestName(String lowestName) { this.lowestName = lowestName; }

	public int getPassedCount() { return passedCount; }
	public void setPassedCount(int passedCount) { this.passedCount = passedCount; }

	public int getFailedCount() { return failedCount; }
	public void setFailedCount(int failedCount) { this.failedCount = failedCount; }

	public double getPassRate() { return passRate; }
	public void setPassRate(double passRate) { this.passRate = passRate; }

	public Map<String, Integer> getGradeDistribution() { return gradeDistribution; }
	public Map<String, Integer> getStatusCounts() { return statusCounts; }

	public List<Student> getHonorRoll() { return honorRoll; }
	public List<Student> getRemediationList() { return remediationList; }
}
