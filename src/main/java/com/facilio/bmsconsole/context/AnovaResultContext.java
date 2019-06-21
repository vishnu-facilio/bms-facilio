package com.facilio.bmsconsole.context;

public class AnovaResultContext {
	String resultVariable;
	int degreeOfFreedom = -1;
	double sumOfSquare = -1;
	double meanSumOfSquare = -1;
	double fStat = -1;
	public String getResultVariable() {
		return resultVariable;
	}
	public void setResultVariable(String resultVariable) {
		this.resultVariable = resultVariable;
	}
	public int getDegreeOfFreedom() {
		return degreeOfFreedom;
	}
	public void setDegreeOfFreedom(int degreeOfFreedom) {
		this.degreeOfFreedom = degreeOfFreedom;
	}
	public double getSumOfSquare() {
		return sumOfSquare;
	}
	public void setSumOfSquare(double sumOfSquare) {
		this.sumOfSquare = sumOfSquare;
	}
	public double getMeanSumOfSquare() {
		return meanSumOfSquare;
	}
	public void setMeanSumOfSquare(double meanSumOfSquare) {
		this.meanSumOfSquare = meanSumOfSquare;
	}
	public double getfStat() {
		return fStat;
	}
	public void setfStat(double fStat) {
		this.fStat = fStat;
	}
}
