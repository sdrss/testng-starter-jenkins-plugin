package com.jenkins.testngstarter;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.google.common.base.Strings;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Maven;
import hudson.util.FormValidation;

public class TestNGstarter extends Builder {
	
	public static final String DISPLAY_NAME = "TestNG Starter";
	public static final String MVN_START_TESTNG_STARTER = "install testng-starter:test -B";
	private String pomLocation;
	private String configFailurePolicy;
	private String dataProviderThreadCount;
	private String groups;
	private String excludedGroups;
	private String failFast;
	private String failOnErrors;
	private String generateHtmlReport;
	private String generateJunitReport;
	private String generateReportNGhtmlReport;
	private String generateXMLReport;
	private String globalTestTimeOut;
	private String handleKnownDefectsAsFailures;
	private String isJUnit;
	private String listeners;
	private String logOutputReport;
	private String outputDirectory;
	private String parallel;
	private String preserveOrder;
	private String randomizeSuites;
	private String reportNGhtmlReportTitle;
	private String reportNGOutputDirectory;
	private String maxTestRetryFailures;
	private String executeTestngFailedxml;
	private String showPassedConfigurations;
	private String threadPoolSize;
	private String suiteThreadPoolSize;
	private String suiteXmlFiles;
	private String systemProperties;
	private String mvnParams;
	
	@DataBoundConstructor
	public TestNGstarter(String pomLocation,
			String configFailurePolicy,
			String dataProviderThreadCount,
			String groups,
			String excludedGroups,
			String failFast,
			String failOnErrors,
			String generateHtmlReport,
			String generateJunitReport,
			String generateReportNGhtmlReport,
			String generateXMLReport,
			String globalTestTimeOut,
			String handleKnownDefectsAsFailures,
			String isJUnit,
			String listeners,
			String logOutputReport,
			String outputDirectory,
			String parallel,
			String preserveOrder,
			String randomizeSuites,
			String reportNGhtmlReportTitle,
			String reportNGOutputDirectory,
			String maxTestRetryFailures,
			String executeTestngFailedxml,
			String showPassedConfigurations,
			String threadPoolSize,
			String suiteThreadPoolSize,
			String suiteXmlFiles,
			String systemProperties,
			String mvnParams) {
		this.setPomLocation(handleParam(pomLocation));
		this.setConfigFailurePolicy(handleParam(configFailurePolicy));
		this.setDataProviderThreadCount(handleParam(dataProviderThreadCount));
		this.setGroups(handleParam(groups));
		this.setExcludedGroups(handleParam(excludedGroups));
		this.setFailFast(handleParam(failFast));
		this.setFailOnErrors(handleParam(failOnErrors));
		this.setGenerateHtmlReport(handleParam(generateHtmlReport));
		this.setGenerateJunitReport(handleParam(generateJunitReport));
		this.setGenerateReportNGhtmlReport(handleParam(generateReportNGhtmlReport));
		this.setGenerateXMLReport(handleParam(generateXMLReport));
		this.setGlobalTestTimeOut(handleParam(globalTestTimeOut));
		this.setHandleKnownDefectsAsFailures(handleParam(handleKnownDefectsAsFailures));
		this.setIsJUnit(handleParam(isJUnit));
		this.setListeners(handleParam(listeners));
		this.setLogOutputReport(handleParam(logOutputReport));
		this.setOutputDirectory(handleParam(outputDirectory));
		this.setParallel(handleParam(parallel));
		this.setPreserveOrder(handleParam(preserveOrder));
		this.setRandomizeSuites(handleParam(randomizeSuites));
		this.setReportNGhtmlReportTitle(handleParam(reportNGhtmlReportTitle));
		this.setReportNGOutputDirectory(handleParam(reportNGOutputDirectory));
		this.setMaxTestRetryFailures(handleParam(maxTestRetryFailures));
		this.setExecuteTestngFailedxml(handleParam(executeTestngFailedxml));
		this.setShowPassedConfigurations(handleParam(showPassedConfigurations));
		this.setThreadPoolSize(handleParam(threadPoolSize));
		this.setSuiteThreadPoolSize(handleParam(suiteThreadPoolSize));
		this.setSuiteXmlFiles(handleParam(suiteXmlFiles));
		this.setSystemProperties(handleParam(systemProperties));
		this.setMvnParams(handleParam(mvnParams));
	}
	
	@Override
	public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
		PrintStream logger = listener.getLogger();
		logger.println("Start TestNG starter plugin");
		String params = generateMvnParams();
		String command = MVN_START_TESTNG_STARTER + params;
		logger.println(command);
		Maven mvn = new Maven(command, "");
		mvn.perform(build, launcher, listener);
		logger.println("Finish TestNG starter plugin");
		return true;
	}
	
	@Symbol("testNGstarter")
	@Extension
	public static class Descriptor extends BuildStepDescriptor<Builder> {
		
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}
		
		@Override
		public String getDisplayName() {
			return DISPLAY_NAME;
		}
		
		// Form validation
		public FormValidation doCheckThreadPoolSize(@QueryParameter String value) throws IOException, ServletException {
			return doCheck(value);
		}
		
		public FormValidation doCheckSuiteThreadPoolSize(@QueryParameter String value) throws IOException, ServletException {
			return doCheck(value);
		}
		
		public FormValidation doCheckDataProviderThreadCount(@QueryParameter String value) throws IOException, ServletException {
			return doCheck(value);
		}
		
		public FormValidation doCheckGlobalTestTimeOut(@QueryParameter String value) throws IOException, ServletException {
			return doCheck(value);
		}
		
		public FormValidation doCheckMaxTestRetryFailures(@QueryParameter String value) throws IOException, ServletException {
			return doCheck(value);
		}
		
		// General validation for int > 0
		private FormValidation doCheck(@QueryParameter String value) throws IOException, ServletException {
			if (!Strings.isNullOrEmpty(value)) {
				try {
					int number = Integer.parseInt(value);
					if (number >= 0) {
						return FormValidation.ok();
					} else {
						return FormValidation.error("Number should be greater or equal to 0");
					}
				} catch (NumberFormatException e) {
					return FormValidation.error("Not a number");
				}
			}
			return FormValidation.ok();
		}
		
		// System properties form validation
		public FormValidation doCheckSystemProperties(@QueryParameter String value) throws IOException, ServletException {
			if (!Strings.isNullOrEmpty(value)) {
				boolean foundError = false;
				String[] elements = value.split(",");
				for (String temp : elements) {
					String[] keyValue = temp.split(":");
					if (keyValue.length != 2) {
						foundError = true;
					}
				}
				if (foundError) {
					return FormValidation.error("No key value pair. Usage key:value");
				}
			}
			return FormValidation.ok();
		}
	}
	
	private String generateMvnParams() {
		StringBuilder params = new StringBuilder();
		if (getPomLocation() != null) {
			params.append(" -f " + getPomLocation() + "");
		}
		if (getConfigFailurePolicy() != null && !getConfigFailurePolicy().equalsIgnoreCase("NONE")) {
			params.append(" -DconfigFailurePolicy=" + getConfigFailurePolicy());
		}
		if (getDataProviderThreadCount() != null) {
			params.append(" -DdataProviderThreadCount=" + getDataProviderThreadCount());
		}
		if (getGroups() != null) {
			params.append(" -Dgroups=" + getGroups());
		}
		if (getExcludedGroups() != null) {
			params.append(" -DexcludedGroups=" + getExcludedGroups());
		}
		if (getFailFast() != null && getFailFast().equalsIgnoreCase("true")) {
			params.append(" -DfailFast=true");
		}
		if (getFailOnErrors() != null && getFailOnErrors().equalsIgnoreCase("true")) {
			params.append(" -DfailOnErrors=true");
		}
		if (getGenerateHtmlReport() != null && getGenerateHtmlReport().equalsIgnoreCase("true")) {
			params.append(" -DgenerateHtmlReport=true");
		}
		if (getGenerateJunitReport() != null && getGenerateJunitReport().equalsIgnoreCase("true")) {
			params.append(" -DgenerateJunitReport=true");
		}
		if (getGenerateReportNGhtmlReport() != null && getGenerateReportNGhtmlReport().equalsIgnoreCase("true")) {
			params.append(" -DgenerateReportNGhtmlReport=true");
		}
		if (getGenerateXMLReport() != null && getGenerateXMLReport().equalsIgnoreCase("true")) {
			params.append(" -DgenerateXMLReport=true");
		}
		if (getGlobalTestTimeOut() != null) {
			try {
				int globalTestTimeOutInt = Integer.parseInt(getGlobalTestTimeOut());
				if (globalTestTimeOutInt > 0) {
					params.append(" -DglobalTestTimeOut=" + getGlobalTestTimeOut());
				}
			} catch (Exception ex) {
			}
		}
		if (getHandleKnownDefectsAsFailures() != null && getHandleKnownDefectsAsFailures().equalsIgnoreCase("true")) {
			params.append(" -DhandleKnownDefectsAsFailures=true");
		}
		if (getIsJUnit() != null && getIsJUnit().equalsIgnoreCase("true")) {
			params.append(" -DisJUnit=true");
		}
		if (getListeners() != null) {
			params.append(" -Dlisteners=" + getListeners());
		}
		if (getLogOutputReport() != null && getLogOutputReport().equalsIgnoreCase("true")) {
			params.append(" -DlogOutputReport=true");
		}
		if (getOutputDirectory() != null) {
			params.append(" -DoutputDirectory=" + getOutputDirectory());
		}
		if (getParallel() != null && !getParallel().equalsIgnoreCase("NONE")) {
			params.append(" -Dparallel=" + getParallel());
		}
		if (getPreserveOrder() != null && getPreserveOrder().equalsIgnoreCase("false")) {
			params.append(" -DpreserveOrder=false");
		}
		if (getRandomizeSuites() != null && getRandomizeSuites().equalsIgnoreCase("true")) {
			params.append(" -DrandomizeSuites=true");
		}
		if (getReportNGhtmlReportTitle() != null) {
			params.append(" -DreportNGhtmlReportTitle=" + getReportNGhtmlReportTitle());
		}
		if (getReportNGOutputDirectory() != null) {
			params.append(" -DreportNGOutputDirectory=" + getReportNGOutputDirectory());
		}
		if (getMaxTestRetryFailures() != null) {
			try {
				int maxTestRetryFailuresInt = Integer.parseInt(getMaxTestRetryFailures());
				if (maxTestRetryFailuresInt > 0) {
					params.append(" -DmaxTestRetryFailures=" + getMaxTestRetryFailures());
				}
			} catch (Exception ex) {
				params.append(" -DmaxTestRetryFailures=0");
			}
		}
		if (getExecuteTestngFailedxml() != null && getExecuteTestngFailedxml().equalsIgnoreCase("true")) {
			params.append(" -DexecuteTestngFailedxml=true");
		}
		if (getShowPassedConfigurations() != null && getShowPassedConfigurations().equalsIgnoreCase("false")) {
			params.append(" -DshowPassedConfigurations=true");
		}
		if (getThreadPoolSize() != null) {
			params.append(" -DthreadPoolSize=" + getThreadPoolSize());
		}
		if (getSuiteThreadPoolSize() != null) {
			params.append(" -DsuiteThreadPoolSize=" + getSuiteThreadPoolSize());
		}
		if (getSuiteXmlFiles() != null) {
			params.append(" -DsuiteXmlFiles=" + getSuiteXmlFiles());
		}
		if (getSystemProperties() != null) {
			params.append(" -DsystemProperties=" + getSystemProperties());
		}
		if (getMvnParams() != null) {
			params.append(" " + getMvnParams());
		}
		return params.toString();
	}
	
	private String handleParam(String param) {
		if (!Strings.isNullOrEmpty(param)) {
			return param.trim();
		}
		return null;
	}
	
	public String getPomLocation() {
		return pomLocation;
	}
	
	public void setPomLocation(String pomLocation) {
		this.pomLocation = pomLocation;
	}
	
	public String getConfigFailurePolicy() {
		return configFailurePolicy;
	}
	
	public void setConfigFailurePolicy(String configFailurePolicy) {
		this.configFailurePolicy = configFailurePolicy;
	}
	
	public String getFailFast() {
		return failFast;
	}
	
	public void setFailFast(String failFast) {
		this.failFast = failFast;
	}
	
	public String getGenerateJunitReport() {
		return generateJunitReport;
	}
	
	public void setGenerateJunitReport(String generateJunitReport) {
		this.generateJunitReport = generateJunitReport;
	}
	
	public String getGenerateHtmlReport() {
		return generateHtmlReport;
	}
	
	public void setGenerateHtmlReport(String generateHtmlReport) {
		this.generateHtmlReport = generateHtmlReport;
	}
	
	public String getFailOnErrors() {
		return failOnErrors;
	}
	
	public void setFailOnErrors(String failOnErrors) {
		this.failOnErrors = failOnErrors;
	}
	
	public String getGenerateReportNGhtmlReport() {
		return generateReportNGhtmlReport;
	}
	
	public void setGenerateReportNGhtmlReport(String generateReportNGhtmlReport) {
		this.generateReportNGhtmlReport = generateReportNGhtmlReport;
	}
	
	public String getGenerateXMLReport() {
		return generateXMLReport;
	}
	
	public void setGenerateXMLReport(String generateXMLReport) {
		this.generateXMLReport = generateXMLReport;
	}
	
	public String getGlobalTestTimeOut() {
		return globalTestTimeOut;
	}
	
	public void setGlobalTestTimeOut(String globalTestTimeOut) {
		this.globalTestTimeOut = globalTestTimeOut;
	}
	
	public String getHandleKnownDefectsAsFailures() {
		return handleKnownDefectsAsFailures;
	}
	
	public void setHandleKnownDefectsAsFailures(String handleKnownDefectsAsFailures) {
		this.handleKnownDefectsAsFailures = handleKnownDefectsAsFailures;
	}
	
	public String getIsJUnit() {
		return isJUnit;
	}
	
	public void setIsJUnit(String isJUnit) {
		this.isJUnit = isJUnit;
	}
	
	public String getListeners() {
		return listeners;
	}
	
	public void setListeners(String listeners) {
		this.listeners = listeners;
	}
	
	public String getLogOutputReport() {
		return logOutputReport;
	}
	
	public void setLogOutputReport(String logOutputReport) {
		this.logOutputReport = logOutputReport;
	}
	
	public String getOutputDirectory() {
		return outputDirectory;
	}
	
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	public String getParallel() {
		return parallel;
	}
	
	public void setParallel(String parallel) {
		this.parallel = parallel;
	}
	
	public String getPreserveOrder() {
		return preserveOrder;
	}
	
	public void setPreserveOrder(String preserveOrder) {
		this.preserveOrder = preserveOrder;
	}
	
	public String getRandomizeSuites() {
		return randomizeSuites;
	}
	
	public void setRandomizeSuites(String randomizeSuites) {
		this.randomizeSuites = randomizeSuites;
	}
	
	public String getReportNGhtmlReportTitle() {
		return reportNGhtmlReportTitle;
	}
	
	public void setReportNGhtmlReportTitle(String reportNGhtmlReportTitle) {
		this.reportNGhtmlReportTitle = reportNGhtmlReportTitle;
	}
	
	public String getReportNGOutputDirectory() {
		return reportNGOutputDirectory;
	}
	
	public void setReportNGOutputDirectory(String reportNGOutputDirectory) {
		this.reportNGOutputDirectory = reportNGOutputDirectory;
	}
	
	public String getMaxTestRetryFailures() {
		return maxTestRetryFailures;
	}
	
	public void setMaxTestRetryFailures(String maxTestRetryFailures) {
		this.maxTestRetryFailures = maxTestRetryFailures;
	}
	
	public String getExecuteTestngFailedxml() {
		return executeTestngFailedxml;
	}
	
	public void setExecuteTestngFailedxml(String executeTestngFailedxml) {
		this.executeTestngFailedxml = executeTestngFailedxml;
	}
	
	public String getShowPassedConfigurations() {
		return showPassedConfigurations;
	}
	
	public void setShowPassedConfigurations(String showPassedConfigurations) {
		this.showPassedConfigurations = showPassedConfigurations;
	}
	
	public String getThreadPoolSize() {
		return threadPoolSize;
	}
	
	public void setThreadPoolSize(String threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}
	
	public String getSuiteThreadPoolSize() {
		return suiteThreadPoolSize;
	}
	
	public void setSuiteThreadPoolSize(String suiteThreadPoolSize) {
		this.suiteThreadPoolSize = suiteThreadPoolSize;
	}
	
	public String getSuiteXmlFiles() {
		return suiteXmlFiles;
	}
	
	public void setSuiteXmlFiles(String suiteXmlFiles) {
		this.suiteXmlFiles = suiteXmlFiles;
	}
	
	public String getSystemProperties() {
		return systemProperties;
	}
	
	public void setSystemProperties(String systemProperties) {
		this.systemProperties = systemProperties;
	}
	
	public String getExcludedGroups() {
		return excludedGroups;
	}
	
	public void setExcludedGroups(String excludedGroups) {
		this.excludedGroups = excludedGroups;
	}
	
	public String getGroups() {
		return groups;
	}
	
	public void setGroups(String groups) {
		this.groups = groups;
	}
	
	public String getDataProviderThreadCount() {
		return dataProviderThreadCount;
	}
	
	public void setDataProviderThreadCount(String dataProviderThreadCount) {
		this.dataProviderThreadCount = dataProviderThreadCount;
	}
	
	public String getMvnParams() {
		return mvnParams;
	}
	
	public void setMvnParams(String mvnParams) {
		this.mvnParams = mvnParams;
	}
}
