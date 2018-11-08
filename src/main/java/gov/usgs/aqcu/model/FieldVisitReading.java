package gov.usgs.aqcu.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ReadingType;

public class FieldVisitReading {

	private Instant visitTime;
	private String visitStatus;
	private List<String> comments = new ArrayList<>();
	private Instant lastVisitPrior; // not used in the actual report, only to get AssociatedIv
	private Instant time;
	private String monitoringMethod;
	private String uncertainty;
	private String value;
	private ReadingType readingType;
	private String party;
	private String sublocation;
	private String associatedIvValue;
	private Instant associatedIvTime;
	private List<AssociatedIvQualifier> associatedIvQualifiers;
	
	public FieldVisitReading(Instant visitTime, String party, String visitStatus, List<String> comments, Reading reading) {		
		this.associatedIvQualifiers = new ArrayList<>();
		this.visitTime = visitTime;
		this.comments = comments;
		this.visitStatus = visitStatus;
		this.time = reading.getTime();
		this.monitoringMethod = reading.getMonitoringMethod();
		this.uncertainty = reading.getUncertainty() != null ? reading.getUncertainty().getDisplay() : null;
		this.value = reading.getValue() != null ? reading.getValue().getDisplay() : null;
		this.party = party;
		this.sublocation = reading.getSubLocationIdentifier();
		this.readingType = reading.getReadingType();
	}

	public FieldVisitReading(Instant visitTime, String party, String visitStatus, List<String> comments, Instant readTime, 
		String monitoringMethod, String uncerntainy, String value, String sublocationIdentifier, ReadingType readingType) 
	{
		this.associatedIvQualifiers = new ArrayList<>();
		this.visitTime = visitTime;
		this.comments = comments;
		this.visitStatus = visitStatus;
		this.time = readTime;
		this.monitoringMethod = monitoringMethod;
		this.uncertainty = uncerntainy;
		this.value = value;
		this.party = party;
		this.sublocation = sublocationIdentifier;
		this.readingType = readingType;
	}

	public Instant getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Instant visitTime) {
		this.visitTime = visitTime;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public Instant getLastVisitPrior() {
		return lastVisitPrior;
	}

	public void setLastVisitPrior(Instant lastVisitPrior) {
		this.lastVisitPrior = lastVisitPrior;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public String getMonitoringMethod() {
		return monitoringMethod;
	}

	public void setMonitoringMethod(String monitoringMethod) {
		this.monitoringMethod = monitoringMethod;
	}

	public String getUncertainty() {
		return uncertainty;
	}

	public void setUncertainty(String uncertainty) {
		this.uncertainty = uncertainty;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParty() {
		return party;
	}

	public void setParty(String party) {
		this.party = party;
	}

	public String getSublocation() {
		return sublocation;
	}

	public void setSublocation(String sublocation) {
		this.sublocation = sublocation;
	}

	public String getAssociatedIvValue() {
		return associatedIvValue;
	}

	public void setAssociatedIvValue(String associatedIvValue) {
		this.associatedIvValue = associatedIvValue;
	}

	public Instant getAssociatedIvTime() {
		return associatedIvTime;
	}

	public void setAssociatedIvTime(Instant associatedIvTime) {
		this.associatedIvTime = associatedIvTime;
	}

	public List<AssociatedIvQualifier> getAssociatedIvQualifiers() {
		return associatedIvQualifiers;
	}

	public void setAssociatedIvQualifiers(List<AssociatedIvQualifier> associatedIvQualifiers) {
		this.associatedIvQualifiers = associatedIvQualifiers;
	}

	public String getVisitStatus() {
		return visitStatus;
	}

	public void setVisitStatus(String visitStatus) {
		this.visitStatus = visitStatus;
	}

	public ReadingType getReadingType() {
		return readingType;
	}

	public void setReadingType(ReadingType readingType) {
		this.readingType = readingType;
	}
}
