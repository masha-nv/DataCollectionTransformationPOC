/*
 *Copyright: USCIS. All Rights Reserved.
 * Redistributions of source code must retain the above copyright notice
 * and the following conditions.
 *
 * All information contained herein is, and remains the property of
 * USCIS. The intellectual and technical concepts contained herein are
 * Proprietary to USCIS. Dissemination of this information or reproduction
 * of this material is strictly forbidden unless prior written permission is
 * obtained from USCIS.
 */
package gov.dhs.uscis.elis2.backend.services.serviceimpl;

import static gov.dhs.uscis.elis2.shared.domain.enumeration.CaseQuestionResponseIndicatorEnum.BLANK;
import static gov.dhs.uscis.elis2.shared.domain.enumeration.CaseQuestionResponseIndicatorEnum.NO;
import static gov.dhs.uscis.elis2.shared.domain.enumeration.CaseQuestionResponseIndicatorEnum.YES;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import gov.dhs.uscis.elis.commons.util.date.DateUtil;
import gov.dhs.uscis.elis2.FeeReductionDTO;
import gov.dhs.uscis.elis2.backend.builders.misc.CaseQuestionResponseBuilder;
import gov.dhs.uscis.elis2.backend.intappservicesinterfaces.EmployeeService;
import gov.dhs.uscis.elis2.backend.services.RelatedBenefitRequestService;
import gov.dhs.uscis.elis2.backend.services.account.AlienNumberService;
import gov.dhs.uscis.elis2.backend.services.caseaction.CaseActionHandler;
import gov.dhs.uscis.elis2.backend.services.casedetail.CaseDetailViewServiceImpl;
import gov.dhs.uscis.elis2.backend.services.casedetail.CaseDetailViewServiceProvider;
import gov.dhs.uscis.elis2.backend.services.ciscase.AccommodationService;
import gov.dhs.uscis.elis2.backend.services.ciscase.CaseDetailService;
import gov.dhs.uscis.elis2.backend.services.ciscase.CaseFlagsService;
import gov.dhs.uscis.elis2.backend.services.ciscase.CaseService;
import gov.dhs.uscis.elis2.backend.services.ciscase.DodDciiService;
import gov.dhs.uscis.elis2.backend.services.ciscase.PacketStatusDataService;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.AddressTypeCodeDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.ApplicationReasonCodeDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.AppointmentDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.BenefitRequestDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.BenefitTypeCodeDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.BirthCountryCodeDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.BondStatusDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CaseAssignmentDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CaseDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CaseEligibilityEmploymentDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CaseFlagIndicatorDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CasePartyRoleDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CasePersonBiographicsDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CasePersonIdentificationDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CasePersonNameDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CasePriorityDateDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CaseQuestionResponseDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CaseSubstatusCodeDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CaseTPSCountryDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.CountryCodeDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.EmailDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.FlagTypeCodeDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.ImmigrationAccountDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.PartyDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.PersonDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.PersonIdentificationDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.PersonNameDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.PhoneDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.SnapshotSignatureDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.TpsCountryDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.UIHeaderSubSectionDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.USCISEmployeeDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.WorksheetHeaderDAO;
import gov.dhs.uscis.elis2.backend.services.dao.jpa.WorksheetStatusDAO;
import gov.dhs.uscis.elis2.backend.services.linked.cases.i485.LinkingPriorCaseHelper;
import gov.dhs.uscis.elis2.backend.services.nctc.NCTCService;
import gov.dhs.uscis.elis2.backend.services.util.CommonServicesConstants;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.AccommodationInfo;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.AlienNumberDataWrapper;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.CaseActionHistoryView;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.CaseDetailView;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.CasePersonSummaryData;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.CaseSummaryData;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.I129CaseDetailView;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.I140CaseDetailView;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.I290BCaseDetailView;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.I539CaseDetailView;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.SignatureDatePopulator;
import gov.dhs.uscis.elis2.internalapp.internalappdomain.eligibility.EligibilityDto;
import gov.dhs.uscis.elis2.service.locationassignment.LocationAssignmentService;
import gov.dhs.uscis.elis2.service.locationassignment.LocationAssignmentServiceImpl;
import gov.dhs.uscis.elis2.service.naturalization.NaturalizationService;
import gov.dhs.uscis.elis2.service.personidentification.PersonIdentificationService;
import gov.dhs.uscis.elis2.service.repository.afile.AFileTransferRequestInformationRepository;
import gov.dhs.uscis.elis2.service.workflow.repository.WorkflowEventRepositoryService;
import gov.dhs.uscis.elis2.shared.domain.dto.CaseFilingCategoryDTO;
import gov.dhs.uscis.elis2.shared.domain.dto.EmploymentAuthorizationDTO;
import gov.dhs.uscis.elis2.shared.domain.dto.EnglishExemptionDTO;
import gov.dhs.uscis.elis2.shared.domain.dto.RevisionHistory;
import gov.dhs.uscis.elis2.shared.domain.dto.SocialSecurityQuestionsDTO;
import gov.dhs.uscis.elis2.shared.domain.dto.caseresolution.VAWAVerificationPayload;
import gov.dhs.uscis.elis2.shared.domain.dto.nta.CourtDTO;
import gov.dhs.uscis.elis2.shared.domain.dto.priorcase.PriorCaseDTO;
import gov.dhs.uscis.elis2.shared.domain.enumeration.ActiveIndicatorEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.AppointmentStatusCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.AppointmentTypeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.BenefitCategoryCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.BenefitTypeCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.BondStatusCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.CaseActionTypeCodeGenericEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.CaseDecisionCdEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.CaseFilingTypeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.CaseQuestionResponseIndicatorEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.CaseStateCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.CaseStatusCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.CaseStatusDTO;
import gov.dhs.uscis.elis2.shared.domain.enumeration.EadEligibilityCategoryCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.EmailType;
import gov.dhs.uscis.elis2.shared.domain.enumeration.FormRevision;
import gov.dhs.uscis.elis2.shared.domain.enumeration.FormType;
import gov.dhs.uscis.elis2.shared.domain.enumeration.I765C09ApplicationTypeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.I765C09FilingCategoryEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.IdentificationTypeCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.IndicatorEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.NCTCResultCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.PersonNameUsageEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.PhoneTypeCode;
import gov.dhs.uscis.elis2.shared.domain.enumeration.RequestAFileTransferStatus;
import gov.dhs.uscis.elis2.shared.domain.enumeration.RoleTypeCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.SourceTypeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.TaskStatus;
import gov.dhs.uscis.elis2.shared.domain.enumeration.TaskType;
import gov.dhs.uscis.elis2.shared.domain.enumeration.TpsCountryEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.UIHeaderSubsectionCode;
import gov.dhs.uscis.elis2.shared.domain.enumeration.UiHeaderCodeEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.formquestions.FormQuestionEnum;
import gov.dhs.uscis.elis2.shared.domain.enumeration.formquestions.N400FormQuestionEnumMap;
import gov.dhs.uscis.elis2.shared.domain.generic.Appointment;
import gov.dhs.uscis.elis2.shared.domain.generic.caseview.CaseView;
import gov.dhs.uscis.elis2.shared.domain.jpa.BenefitPacket;
import gov.dhs.uscis.elis2.shared.domain.jpa.BenefitRequest;
import gov.dhs.uscis.elis2.shared.domain.jpa.BenefitTypeCode;
import gov.dhs.uscis.elis2.shared.domain.jpa.Case;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseAlienNumber;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseAssignment;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseDec;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseEligibilityEmployment;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseFlagIndicator;
import gov.dhs.uscis.elis2.shared.domain.jpa.CasePartyRole;
import gov.dhs.uscis.elis2.shared.domain.jpa.CasePersonBiographics;
import gov.dhs.uscis.elis2.shared.domain.jpa.CasePersonBirthDate;
import gov.dhs.uscis.elis2.shared.domain.jpa.CasePersonIdentification;
import gov.dhs.uscis.elis2.shared.domain.jpa.CasePriorityDate;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseQuestionResponse;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseStateCode;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseStatusCode;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseSubstatusCode;
import gov.dhs.uscis.elis2.shared.domain.jpa.CaseTpsCountry;
import gov.dhs.uscis.elis2.shared.domain.jpa.DACACase;
import gov.dhs.uscis.elis2.shared.domain.jpa.Email;
import gov.dhs.uscis.elis2.shared.domain.jpa.FlagTypeCode;
import gov.dhs.uscis.elis2.shared.domain.jpa.FormQuestion;
import gov.dhs.uscis.elis2.shared.domain.jpa.G325RCase;
import gov.dhs.uscis.elis2.shared.domain.jpa.I129Case;
import gov.dhs.uscis.elis2.shared.domain.jpa.I129H1BCase;
import gov.dhs.uscis.elis2.shared.domain.jpa.I130Case;
import gov.dhs.uscis.elis2.shared.domain.jpa.I134AFRPCase;
import gov.dhs.uscis.elis2.shared.domain.jpa.I134ATACase;
import gov.dhs.uscis.elis2.shared.domain.jpa.I134UHPCase;
import gov.dhs.uscis.elis2.shared.domain.jpa.I140Case;
import gov.dhs.uscis.elis2.shared.domain.jpa.I290BCase;
import gov.dhs.uscis.elis2.shared.domain.jpa.I485Case;
import gov.dhs.uscis.elis2.shared.domain.jpa.I539Case;
import gov.dhs.uscis.elis2.shared.domain.jpa.ImmigrationAccount;
import gov.dhs.uscis.elis2.shared.domain.jpa.LocationCode;
import gov.dhs.uscis.elis2.shared.domain.jpa.PacketStatus;
import gov.dhs.uscis.elis2.shared.domain.jpa.Party;
import gov.dhs.uscis.elis2.shared.domain.jpa.Person;
import gov.dhs.uscis.elis2.shared.domain.jpa.PersonBirthDate;
import gov.dhs.uscis.elis2.shared.domain.jpa.PersonIdentification;
import gov.dhs.uscis.elis2.shared.domain.jpa.PersonName;
import gov.dhs.uscis.elis2.shared.domain.jpa.Phone;
import gov.dhs.uscis.elis2.shared.domain.jpa.QuestionResponseIndicatorCode;
import gov.dhs.uscis.elis2.shared.domain.jpa.RelatedBenefitRequest;
import gov.dhs.uscis.elis2.shared.domain.jpa.RoleTypeCode;
import gov.dhs.uscis.elis2.shared.domain.jpa.SnapshotSignature;
import gov.dhs.uscis.elis2.shared.domain.jpa.TPSCase;
import gov.dhs.uscis.elis2.shared.domain.jpa.USCISEmployee;
import gov.dhs.uscis.elis2.shared.domain.jpa.WorksheetStatus;
import gov.dhs.uscis.elis2.shared.domain.jpa.afile.AFileTransferRequestInformation;
import gov.dhs.uscis.elis2.shared.domain.jpa.usertask.UserTask;
import gov.dhs.uscis.elis2.shared.domain.linked.cases.LinkedCaseDetailsBase;
import gov.dhs.uscis.elis2.shared.domain.utils.NameValuePair;
import gov.dhs.uscis.elis2.shared.libs.errorhandler.BusinessException;
import gov.dhs.uscis.elis2.shared.libs.errorhandler.SystemException;
import gov.dhs.uscis.elis2.shared.libs.service.BenefitPacketService;
import gov.dhs.uscis.elis2.shared.libs.service.BenefitRequestService;
import gov.dhs.uscis.elis2.shared.libs.service.VAWACommonService;
import gov.dhs.uscis.elis2.shared.libs.service.generic.logging.aspect.LoggingContext;
import gov.dhs.uscis.elis2.shared.libs.service.generic.logging.aspect.LoggingPoi;
import gov.dhs.uscis.elis2.shared.libs.service.rest.RestOperationFacade;
import gov.dhs.uscis.elis2.shared.security.access.expression.CustomSecurityExpressionRoot;
import gov.dhs.uscis.elis2.shared.security.serviceimpl.SecurityContextService;
import gov.uscis.elis2.shared.domain.rules.facts.enums.FilingCategoryEnum;
import gov.uscis.elis2.shared.domain.rules.facts.enums.SQAFlagEnum;

@Lazy
@Service
public class CaseDetailServiceImpl implements CaseDetailService {

	private static final Logger logger = LoggerFactory.getLogger(CaseDetailServiceImpl.class);

	private static final String ACCOMMODATIONS_REQUESTED = "Accommodations requested";

	private static final String NOT_APPLICABLE = "N/A";
	
	private static final String HEARING_IMPAIRMENT = "Accommodation Deaf / Hard of Hearing";
	
	private static final String VISUAL_IMPAIRMENT = "Accommodation Blind / Sight Impaired";
	
	private static final String OTHER_DISABILITY = "Other Disability / Impairment";
	
	private static final String NOTAPP = "N/A";
	
	private static final String NULL = "NULL";
	
	private static final String NIA = "NIA";
	
	private static final String NA = "NA";
	
	private static final String NIH = "NIH";
	
	private static final String NH = "NH";
	
	private static final String NID = "NID";
	
	private static final String NM = "NM";
	
	private static final String VIA = "VIA";
	
	private static final String NLA = "NLA";
	
	private static final String N = "N";
	
	private static final String NTA = "NTA";

	private static final String V = "V";
	
	private static final String DATE_FORMAT = "MM/dd/yyyy";
	
	
	public static final int SERVES_ARMED_FORCES_QUESTION = 1272;
	public static final int ACCOMPANY_PARENT_QUESTION = 1273;
	
	
	private static final ImmutableSet<String> OTHER_DISABILITY_EXP = ImmutableSet.of(NOTAPP, NULL, NIA, NA,NIH, NH,NID,NM, VIA,NLA, N, NTA, V);

	private static final List<String> allowableNForms = Collections
			.unmodifiableList(Arrays.asList(FormType.N400.getName(), FormType.N600.getName(), FormType.N600K.getName(),
					FormType.N336.getName(), FormType.N565.getName()));
	
	private static final List<String> allowableIForms = Collections.unmodifiableList(Arrays.asList(FormType.I485.getName(), FormType.I751.getName()));

	private static final ThreadLocal<SimpleDateFormat> dobDateFormatter = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_FORMAT);
		}
	};
	
	private static final List<FormQuestionEnum> SSN_QUESTIONS_ENUMS_I485 = Collections.unmodifiableList(Arrays.asList(
			FormQuestionEnum.I485_EVER_OFFICIALLY_ISSUED_SOCIAL_SECURITY_CARD, 
			FormQuestionEnum.I485_WANTS_SSA_TO_ISSUE_SOCIAL_SECURITY_CARD,
			FormQuestionEnum.I485_HAS_CONSENT_FOR_DISCLOSURE));
	
	private static final List<FormQuestionEnum> SSN_QUESTIONS_ENUMS_I765 = Collections.unmodifiableList(Arrays.asList(
			FormQuestionEnum.EVER_ISSUED_SOCIAL_SECURITY_NUMBER, 
			FormQuestionEnum.REQUESTING_SOCIAL_SECURITY_NUMBER,
			FormQuestionEnum.CONSENT_FOR_DISCLOSURE_SSA));

	private static final List<FormQuestionEnum> SSN_QUESTIONS_ENUMS_G325A = Collections.unmodifiableList(Arrays.asList(
			FormQuestionEnum.REQUESTING_SOCIAL_SECURITY_NUMBER,
			FormQuestionEnum.CONSENT_FOR_DISCLOSURE_SSA));
	
	private static final ImmutableMap<FormQuestionEnum, UIHeaderSubsectionCode> formQuestionToUiHeaderSubSection;
	static {
		Map<FormQuestionEnum, UIHeaderSubsectionCode> values = new EnumMap<>(FormQuestionEnum.class);
		values.put(FormQuestionEnum.I485_EVER_OFFICIALLY_ISSUED_SOCIAL_SECURITY_CARD, UIHeaderSubsectionCode.HAS_SSA_ISSUED_SOCIAL_SECURITY_CARD);
		values.put(FormQuestionEnum.I485_WANTS_SSA_TO_ISSUE_SOCIAL_SECURITY_CARD, UIHeaderSubsectionCode.ISSUE_NEW_SOCIAL_SECURITY_CARD);
		values.put(FormQuestionEnum.I485_HAS_CONSENT_FOR_DISCLOSURE, UIHeaderSubsectionCode.DISCLOSE_INFO_TO_SSA);
		values.put(FormQuestionEnum.EVER_ISSUED_SOCIAL_SECURITY_NUMBER, UIHeaderSubsectionCode.HAS_SSA_ISSUED_SOCIAL_SECURITY_CARD);
		values.put(FormQuestionEnum.REQUESTING_SOCIAL_SECURITY_NUMBER, UIHeaderSubsectionCode.ISSUE_NEW_SOCIAL_SECURITY_CARD);
		values.put(FormQuestionEnum.CONSENT_FOR_DISCLOSURE_SSA, UIHeaderSubsectionCode.DISCLOSE_INFO_TO_SSA);
		
		formQuestionToUiHeaderSubSection = Maps.immutableEnumMap(values);
	}
	
	private static final Map<Integer, I765C09ApplicationTypeEnum> c09FlagToApplicationType = ImmutableMap.<Integer, I765C09ApplicationTypeEnum>builder()
			.put(SQAFlagEnum.I485_APPLICATION_TYPE_FAMILY_BASED.value(), I765C09ApplicationTypeEnum.FAMILY_BASED)
			.put(SQAFlagEnum.I485_EMPLOYMENT_BASED.value(), I765C09ApplicationTypeEnum.EMPLOYMENT_BASED)
			.put(SQAFlagEnum.I485_APPLICATION_TYPE_SPECIAL_IMMIGRANT.value(), I765C09ApplicationTypeEnum.SPECIAL_IMMIGRANT)
			.put(SQAFlagEnum.I485_APPLICATION_TYPE_ASYLEE_OR_REFUGEE.value(), I765C09ApplicationTypeEnum.ASYLEE_REFUGEE)
			.put(SQAFlagEnum.I485_APPLICATION_TYPE_SPECIAL_PROGRAMS.value(), I765C09ApplicationTypeEnum.SPECIAL_PROGRAMS)
			.put(SQAFlagEnum.I485_APPLICATION_TYPE_OTHER.value(), I765C09ApplicationTypeEnum.OTHER)
			.put(SQAFlagEnum.I485_APPLICATION_TYPE_TRAFFICKING_CRIME.value(), I765C09ApplicationTypeEnum.HUMAN_TRAFFICKING_CRIME)
			.build();
	
	private static final Map<Integer, I765C09FilingCategoryEnum> c09FlagToFilingCategory = ImmutableMap.<Integer, I765C09FilingCategoryEnum>builder()
			.put(SQAFlagEnum.I485_FILING_CATEGORY_AFGHAN_IRAQI.value(), I765C09FilingCategoryEnum.AFGHAN_IRAQI)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_ASYLUM.value(), I765C09FilingCategoryEnum.ASYLEE)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_BORN_DIPLOMATIC_STATUS.value(), I765C09FilingCategoryEnum.BORN_DIPLOMATIC)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_CONTINUOUS_RESIDENCY.value(), I765C09FilingCategoryEnum.CONTINUOUS_RESIDENCE)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_CUBAN.value(), I765C09FilingCategoryEnum.CUBAN)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_DIPLOMATS.value(), I765C09FilingCategoryEnum.DIPLOMATS)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_DIVERISITY_VISA.value(), I765C09FilingCategoryEnum.DIVERSITY)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_EOIR.value(), I765C09FilingCategoryEnum.EOIR)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_FIANCEE.value(), I765C09FilingCategoryEnum.FIANCE)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_IMMEDIATE_RELATIVE.value(), I765C09FilingCategoryEnum.IMMEDIATE_RELATIVE)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_LAUTENBERG.value(), I765C09FilingCategoryEnum.LAUTENBERG)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_OTHER_LPR_RELATIVE.value(), I765C09FilingCategoryEnum.OTHER_RELATIVE)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_REFUGEE.value(), I765C09FilingCategoryEnum.REFUGEE)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_SIJ.value(), I765C09FilingCategoryEnum.JUVENILE)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_WIDOW.value(), I765C09FilingCategoryEnum.WIDOW)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_RELIGIOUS_WORKER.value(), I765C09FilingCategoryEnum.RELIGIOUS_WORKER)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_G4_NATO6.value(), I765C09FilingCategoryEnum.G4_NATO6_MEMBERS)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_INTL_BROADCASTER.value(), I765C09FilingCategoryEnum.INTL_BROADCASTER)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_HAITIAN.value(), I765C09FilingCategoryEnum.HAITIAN)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_INDOCHINESE.value(), I765C09FilingCategoryEnum.INDOCHINESE)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_T_NONIMMIGRANT.value(), I765C09FilingCategoryEnum.T_NONIMMIGRANT)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_U_NONIMMIGRANT.value(), I765C09FilingCategoryEnum.U_NONIMMIGRANT)
			.put(SQAFlagEnum.I485_FILING_CATEGORY_VAWA.value(), I765C09FilingCategoryEnum.VAWA)
			.build();
	

	@Inject
	protected ImmigrationAccountDAO immigrationAccountDAO;

	@Inject
	AddressTypeCodeDAO addressTypeCodeDAO;

	@Inject
	ApplicationReasonCodeDAO applicationReasonCodeDAO;

	@Inject
	CaseDAO caseDAO;

	@Inject
	BenefitRequestDAO benefitRequestDAO;

	@Inject
	CountryCodeDAO countryCodeDAO;

	@Inject
	EmailDAO emailDAO;

	@Inject
	PhoneDAO phoneDAO;

	@Inject
	CasePersonNameDAO casePersonNameDAO;

	@Inject
	protected Mapper dozerBeanMapper;

	@Inject
	protected PersonDAO personDAO;

	@Inject
	protected CaseAssignmentDAO caseAssignmentDAO;

	@Inject
	protected PersonNameDAO personNameDAO;

	@Inject
	protected BirthCountryCodeDAO birthCountryCodeDAO;

	@Inject
	protected CaseSubstatusCodeDAO caseSubstatusCodeDAO;

	@Inject
	protected AlienNumberService alienNumberService;

	@Inject
	PacketStatusDataService packetStatusDataService;

	@Inject
	protected CasePartyRoleDAO casePartyRoleDAO;

	@Inject
	protected PartyDAO partyDAO;

	@Inject
	CaseService caseService;
	
	@Inject	
	AccommodationService accommodationService;

	@Inject
	AppointmentDAO appointmentDAO;

	@Inject
	private AFileTransferRequestInformationRepository transferRequestDAO;

	@Inject
	private WorkflowEventRepositoryService workflowEventRepositoryService;

	@Resource(name = "NaturalizationService")
	protected NaturalizationService naturalizationService;

	@Inject
	private CaseQuestionResponseDAO caseQuestionResponseDAO;

	@Inject
	private SecurityContextService securityContextService;

	@Inject
	private EmployeeService employeeService;

	@Inject
	private BenefitRequestService benefitRequestService;

	@Inject
	private DodDciiService dodDciiService;

	@Inject
	private CasePersonBiographicsDAO casePersonBiographicsDAO;

	@Inject
	private SnapshotSignatureDAO snapshotSignatureDAO;

	@Inject
	private VAWACommonService vawaCommonService;

	@Inject
	private BenefitPacketService benefitPacketService;
	
	@Resource
	protected LocationAssignmentService locationAssignmentService;
	
	/**
	 * The non-null {@link RestOperationFacade} instance to use to both get and
	 * patch {@link EligibilityDto} instances associated with a {@link Case}.
	 */
	@Resource(name = "eligibilityDtoRestOperationFacade")
	private RestOperationFacade<EligibilityDto, Long> eligibilityDtoRestOperationFacade;

	@Inject
	TpsCountryDAO tpsCountryDAO;

	@Inject
	CaseTPSCountryDAO caseTpsCountryDAO;
	
	@Inject
	private CasePriorityDateDAO casePriorityDateDAO;

	@Inject
	@Lazy
	private NCTCService nctcService;

	@Inject
	CaseDetailViewServiceProvider caseDetailViewServiceLocator;
	
	@Inject
	private BondStatusDAO bondStatusDAO;
	
	@Inject
	private CasePersonIdentificationDAO casePersonIdentificationDAO;
	
	@Inject
	private PersonIdentificationDAO personIdentificationDAO;
	
	@Inject
	private UIHeaderSubSectionDAO uiHeaderSubSectionDAO;
	
	@Inject
	private WorksheetHeaderDAO worksheetHeaderDAO;
	
	@Inject
	private WorksheetStatusDAO worksheetStatusDAO;
	
	@Inject
	private CaseFlagsService caseFlagsService;
	
	@Inject
	private CaseFlagIndicatorDAO caseFlagIndicatorDAO;
	
	@Inject
	private FlagTypeCodeDAO flagTypeCodeDAO;
	
	@Inject
	private CaseActionHandler caseActionHandler;
	
	@Inject
	private LinkingPriorCaseHelper linkingPriorCaseHelper;
	
	@Inject
	private RelatedBenefitRequestService relatedBenefitRequestService;
	
	@Inject
	private CaseEligibilityEmploymentDAO caseEligibilityEmploymentDAO;
	
	@Inject
	private PersonIdentificationService personIdentificationService;

	@Inject
	private BenefitTypeCodeDAO benefitTypeCodeDAO;
	
	@Inject
	private USCISEmployeeDAO uscisEmployeeDAO;	

	@Transactional
	@Override
	@LoggingContext({ @LoggingPoi(name = "caseId") })
	public RevisionHistory<String> getN400DisabilityWaivers(Long caseId) {
		RevisionHistory<String> disabilityWaivers = new RevisionHistory<>(null);
		Case cisCase = caseDAO.findOne(caseId);
		String receiptNumber = cisCase.getUscisReceiptNumber();
		BenefitRequest benefit = benefitRequestDAO.findByUscisReceiptNumber(receiptNumber);
		if (isNaturalizationForm(benefit)) {
			FormRevision formRevisionEnum = benefitRequestService.getFormRevision(caseId);
			int disabilityQuestion = FormQuestionEnum.DISABILITY_WAIVER.getId(formRevisionEnum);
			List<CaseQuestionResponse> listOfRepsonses = caseQuestionResponseDAO
					.findByCaseIdentifierAndQuestionId(caseId, disabilityQuestion);
			if (!listOfRepsonses.isEmpty()) {
				CaseQuestionResponse questionResponse = listOfRepsonses.get(0);
				if (isNotBlankIndicator(questionResponse)) {
					disabilityWaivers
							.setItem(questionResponse.getQuestionResponseIndicator().getQuestnResponseIndicatorDesc());
				}
			}
		}
		return disabilityWaivers;
	}

	private boolean isNotBlankIndicator(CaseQuestionResponse questionResponse) {
		return isValidQuestionResponseIndicator(questionResponse)
				&& !Objects.equals(questionResponse.getQuestionResponseIndicator().getQuestnResponseIndicatorCd(),
						BLANK.getValue());
	}

	private boolean isNaturalizationForm(BenefitRequest benefit) {
		return benefit.getBenefitTypeCode().getBenefitTypeCode() == BenefitTypeCodeEnum.APPLICATION_FOR_NATURALIZATION
				.getValue();
	}

	@Transactional
	@Override
	@LoggingContext({ @LoggingPoi(name = "caseId") })
	public EnglishExemptionDTO getN400EnglishExemptions(Long caseId) {
		Case cisCase = caseDAO.findOne(caseId);
		String receiptNumber = cisCase.getUscisReceiptNumber();
		BenefitRequest benefit = benefitRequestDAO.findByUscisReceiptNumber(receiptNumber);
		EnglishExemptionDTO exemptions = new EnglishExemptionDTO();
		if (isNaturalizationForm(benefit) &&  FormRevision.N400_FORM_REVISION_231 != cisCase.getBenefitRequest().getRevision()) {
			assembleFormQuestionsByAge(caseId, exemptions);
		}
		return exemptions;
	}

	@Transactional
	@Override
	@LoggingContext({ @LoggingPoi(name = "caseId") })
	public EmploymentAuthorizationDTO getG325AEmploymentAuthorization(long caseId) {
		
		Case cisCase = caseDAO.findOne(caseId);
		EmploymentAuthorizationDTO employmentAuthorizationDTO = new EmploymentAuthorizationDTO();
		if (FormRevision.G325A_FORM_REVISION_257 == cisCase.getBenefitRequest().getRevision()) {
			getG325AEmploymentAuthorization(caseId, employmentAuthorizationDTO);
		}
		return employmentAuthorizationDTO;
	}
	
	private void getG325AEmploymentAuthorization(long caseId, EmploymentAuthorizationDTO employmentAuthorizationDTO) {
		
		FormRevision formRevisionEnum = benefitRequestService.getFormRevision(caseId);
		int requestEADId = FormQuestionEnum.G325A_REQUESTING_EAD.getId(formRevisionEnum);
		employmentAuthorizationDTO.setRequestingEADId(requestEADId);
		
		CaseQuestionResponse requestEAD =  findByCaseIdAndQuestionId(caseId, requestEADId);
		
		employmentAuthorizationDTO.setRequestEAD(requestEAD != null? requestEAD.getAsTrueFalse(): false);
		
		int annualIncomeId = FormQuestionEnum.G325A_CURRENT_ANNUAL_INCOME.getId(formRevisionEnum);
		employmentAuthorizationDTO.setCurrentAnnualIncomeId(annualIncomeId);
		
		CaseQuestionResponse annualIncome =  findByCaseIdAndQuestionId(caseId, annualIncomeId);
		
		if (annualIncome != null) {
			employmentAuthorizationDTO.setCurrentAnnualIncome(annualIncome.getResponseText());
		}
		
		int annualExpenseId = FormQuestionEnum.G325A_CURRENT_ANNUAL_EXPENSES.getId(formRevisionEnum);
		employmentAuthorizationDTO.setCurrentAnnualExpenseId(annualExpenseId);
		
		CaseQuestionResponse annualExpense =  findByCaseIdAndQuestionId(caseId, annualExpenseId);
		if (annualExpense != null) {
			employmentAuthorizationDTO.setCurrentAnnualExpense(annualExpense.getResponseText());
		}
		
		int currentAssetId = FormQuestionEnum.G325A_CURRENT_ASSET_VALUE.getId(formRevisionEnum);
		employmentAuthorizationDTO.setCurrentValueOfAssetsId(currentAssetId);
		
		CaseQuestionResponse currentAssets =  findByCaseIdAndQuestionId(caseId, currentAssetId);
		if (currentAssets != null) {
			employmentAuthorizationDTO.setCurrentValueOfAssets(currentAssets.getResponseText());
		}
		
		
	}

	@Transactional
	@Override
	@LoggingContext({ @LoggingPoi(name = "caseId") })
	public FeeReductionDTO getN400FeeReduction(long caseId) {
		Case cisCase = caseDAO.findOne(caseId);
		String receiptNumber = cisCase.getUscisReceiptNumber();
		BenefitRequest benefit = benefitRequestDAO.findByUscisReceiptNumber(receiptNumber);
		FeeReductionDTO feeReduction = new FeeReductionDTO();
		if (isNaturalizationForm(benefit) &&  FormRevision.N400_FORM_REVISION_231 == cisCase.getBenefitRequest().getRevision()) {
			assembleFeeReduction(caseId, feeReduction);
		}
		return feeReduction;
	}

	private void assembleFeeReduction(long caseId, FeeReductionDTO feeReduction) {
		FormRevision formRevisionEnum = benefitRequestService.getFormRevision(caseId);
		int houseHoldIncomeLessThan = N400FormQuestionEnumMap.REQUEST_FOR_FEE_REDUCTION_HOUSE_HOLD_INCOME_LESS_THAN.getId(formRevisionEnum);
		feeReduction.setHouseHoldIncomeLessThanQuestionId(houseHoldIncomeLessThan);
		CaseQuestionResponse isHouseHoldIncomeLessThan =  findByCaseIdAndQuestionId(caseId, houseHoldIncomeLessThan);
		if (isHouseHoldIncomeLessThan != null) {
			feeReduction.setHouseHoldIncomeLessThanQuestionResponseId(isHouseHoldIncomeLessThan.getCaseQuestionResponseId());
			feeReduction.setHouseHoldIncomeLessThanQuestionResponseValue(isValidQuestionResponseIndicator(isHouseHoldIncomeLessThan) ? isHouseHoldIncomeLessThan.getAsTrueFalse(): null);
		}
		int totalHouseHoldIncomeQuestionId = N400FormQuestionEnumMap.REQUEST_FOR_FEE_REDUCTION_TOTAL_HOUSE_HOLD_INCOME.getId(formRevisionEnum);
		feeReduction.setHouseHoldIncomeQuestionId(totalHouseHoldIncomeQuestionId);
		CaseQuestionResponse houseHoldIncome =  findByCaseIdAndQuestionId(caseId, totalHouseHoldIncomeQuestionId);
		if (houseHoldIncome != null) {
			feeReduction.setHouseHoldIncomeResponseId(houseHoldIncome.getCaseQuestionResponseId());
			feeReduction.setHouseHoldIncome(houseHoldIncome.getResponseText());
		}
		int houseHoldSizeId = N400FormQuestionEnumMap.REQUEST_FOR_FEE_REDUCTION_HOUSE_HOLD_SIZE.getId(formRevisionEnum);
		feeReduction.setHouseHoldSizeQuestionId(houseHoldSizeId);
		CaseQuestionResponse houseHoldSize =  findByCaseIdAndQuestionId(caseId, houseHoldSizeId);
		if (houseHoldSize != null) {
			feeReduction.setHouseHoldSizeResponseId(houseHoldSize.getCaseQuestionResponseId());
			feeReduction.setHouseHoldSize(houseHoldSize.getResponseText());
		}
		int totalNumberOfHouseholdEarningIncomeQuestionId = N400FormQuestionEnumMap.REQUEST_FOR_FEE_REDUCTION_HOUSE_MEMBERS_EARNING_INCOME.getId(formRevisionEnum);
		feeReduction.setNumberOfHouseholdEarningIncomeQuestionId(totalNumberOfHouseholdEarningIncomeQuestionId);
		CaseQuestionResponse totalHouseholdNumberEarningIncome =  findByCaseIdAndQuestionId(caseId, totalNumberOfHouseholdEarningIncomeQuestionId);
		if (totalHouseholdNumberEarningIncome != null) {
			feeReduction.setTotalNumberOfHouseEarningIncomeResponseId(totalHouseholdNumberEarningIncome.getCaseQuestionResponseId());
			feeReduction.setTotalNumberOfHouseHoldEarningIncome(totalHouseholdNumberEarningIncome.getResponseText());
		}
		int headOfHouseHoldQuestionId = N400FormQuestionEnumMap.REQUEST_FOR_FEE_REDUCTION_HEAD_OF_HOUSE.getId(formRevisionEnum);
		feeReduction.setHeadOfHouseholdQuestionId(headOfHouseHoldQuestionId);
		CaseQuestionResponse headOfHouseHold =  findByCaseIdAndQuestionId(caseId, headOfHouseHoldQuestionId);
		if (headOfHouseHold != null) {
			feeReduction.setHeadOfHouseHoldResponseId(headOfHouseHold.getCaseQuestionResponseId());
			feeReduction.setHeadOfHouseHold(isValidQuestionResponseIndicator(headOfHouseHold) ? headOfHouseHold.getAsTrueFalse(): null);
		}
		int nameOfHouseHoldQuestionId = N400FormQuestionEnumMap.REQUEST_FOR_FEE_REDUCTION_NAME_OF_HOUSE_HOLD.getId(formRevisionEnum);
		feeReduction.setNameOfHouseholdQuestionId(nameOfHouseHoldQuestionId);
		CaseQuestionResponse nameOfHouseHold =  findByCaseIdAndQuestionId(caseId, nameOfHouseHoldQuestionId);
		if (nameOfHouseHold != null) {
			feeReduction.setHouseholdNameResponseId(nameOfHouseHold.getCaseQuestionResponseId());
			feeReduction.setHouseholdName(nameOfHouseHold.getResponseText());
		}

	}

	private void assembleFormQuestionsByAge(Long caseId, EnglishExemptionDTO exemptions) {
		FormRevision formRevisionEnum = benefitRequestService.getFormRevision(caseId);

		int over50Question = FormQuestionEnum.IS_APPLICANT_OVER_50.getId(formRevisionEnum);
		CaseQuestionResponse appOver50 = findByCaseIdAndQuestionId(caseId, over50Question);
		if (isValidQuestionResponseIndicator(appOver50)) {
			exemptions.setAppOver50(appOver50.getQuestionResponseIndicator().getQuestnResponseIndicatorDesc());
		}

		int over55Question = FormQuestionEnum.IS_APPLICANT_OVER_55.getId(formRevisionEnum);
		CaseQuestionResponse appOver55 = findByCaseIdAndQuestionId(caseId, over55Question);
		if (isValidQuestionResponseIndicator(appOver55)) {
			exemptions.setAppOver55(appOver55.getQuestionResponseIndicator().getQuestnResponseIndicatorDesc());
		}

		int over65Question = FormQuestionEnum.IS_APPLICANT_OVER_65.getId(formRevisionEnum);
		CaseQuestionResponse appOver65 = findByCaseIdAndQuestionId(caseId, over65Question);
		if (isValidQuestionResponseIndicator(appOver65)) {
			exemptions.setAppOver65(appOver65.getQuestionResponseIndicator().getQuestnResponseIndicatorDesc());
		}
	}

	private boolean isValidQuestionResponseIndicator(CaseQuestionResponse caseResponse) {
		return caseResponse != null && caseResponse.getQuestionResponseIndicator() != null;
	}

	private CaseQuestionResponse findByCaseIdAndQuestionId(Long caseId, int questionId) {
		return caseQuestionResponseDAO.findOneByCaseIdentifierAndQuestionId(caseId, questionId);
	}

	@Override
	@LoggingContext({ @LoggingPoi(name = "uuid"), @LoggingPoi(index = 1, name = "caseId") })
	public Boolean getExternalUserHasCasePartyRole(String uuid, Long caseId) {
		ImmigrationAccount immigrationAccount = getImmigrationAccountByUserId(uuid);

		if (immigrationAccount == null) {
			// the uuid is invalid or the immigration account hasn't been
			// created yet (doesn't have an operationalized case)
			return Boolean.FALSE;
		}
		/**
		 * Case caseObj = caseDAO.findByIdAndCasePartyRole(caseId,
		 * immigrationAccount.getParty().getPartyIdentifier());
		 **/
		long casePRCount = caseDAO.findByIdAndCasePartyRoleCount(caseId,
				immigrationAccount.getParty().getPartyIdentifier());

		// return caseObj != null;
		return casePRCount > 0;
	}

	private ImmigrationAccount getImmigrationAccountByUserId(String uuid) {
		List<ImmigrationAccount> immigrationAccountList = immigrationAccountDAO.findAllByIcamUniqueUserId(uuid);
		ImmigrationAccount immigrationAccount = null;
		if (immigrationAccountList != null) {
			immigrationAccount = immigrationAccountList.stream().findFirst().orElse(null);
		}
		return immigrationAccount;
	}

	@Override
	public Boolean getExternalUserHasCasePartyRole(String uuid, String receiptNumber) {
		ImmigrationAccount immigrationAccount = null;

		try {
			immigrationAccount = immigrationAccountDAO.findByIcamUniqueUserId(uuid);
		} catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
			logger.info(e.getMessage());
			immigrationAccount = getImmigrationAccountByUserId(uuid);
		}
		if (immigrationAccount == null) {
			// the uuid is invalid or the immigration account hasn't been
			// created yet (doesn't have an operationalized case)
			return Boolean.FALSE;
		}
		Case caseObj = caseDAO.findByUSCISReceiptNumberAndCasePartyRole(receiptNumber,
				immigrationAccount.getParty().getPartyIdentifier());
		return caseObj != null;
	}

	@Override
	@Transactional
	@LoggingContext({ @LoggingPoi(name = "caseId") })
	public CaseDetailView getCaseDetail(Long caseId) {
		Case cisCase = caseDAO.findOne(caseId);
		return caseDetailViewServiceLocator.get(cisCase.getFormType()).getCaseDetailView(cisCase);
	}

	@Override
	@Transactional
	@LoggingContext
	public List<LinkedCaseDetailsBase> getLinkedCases(Case cisCase, String linkType) {
		return caseDetailViewServiceLocator.get(cisCase.getFormType()).getLinkedCases(cisCase, linkType);
	}

	@Override
	@LoggingContext
	public Case findOneByUscisReceiptNumber(String uscisReceiptNumber) {
		return caseDAO.findOneByUscisReceiptNumber(uscisReceiptNumber);
	}

	@Override
	@Transactional
	@LoggingContext({ @LoggingPoi(name = "caseId") })
	public FormType getFormType(Long caseId) {
		Case cisCase = Validate.notNull(caseDAO.findOne(caseId), "Could not find case with ID %s", caseId);
		return Validate.notNull(cisCase.getFormType());
	}

	@Override
	@Transactional
	@LoggingContext({ @LoggingPoi(name = "caseId") })
	public CaseSummaryData findCaseSummaryDataLightByCaseID(long caseID) {
		Case cisCase = caseDAO.findOne(caseID);

		CaseSummaryData caseSummaryData = new CaseSummaryData();
		caseSummaryData.setFcoJurisdiction(cisCase.getJurisdictionLocation() == null ? null
				: new NameValuePair<>(cisCase.getJurisdictionLocation().getLctnCd(),
						cisCase.getJurisdictionLocation().getLctnCdDesc()));

		long personId = getCaseApplicant(cisCase).getPartyIdentifier();

		Person person = personDAO.findOne(personId);
		getAlienNumberInfo(person, cisCase, caseSummaryData);
		
		setFullNameOntoCaseSummaryData(person.getPersonNames(), caseSummaryData);

		if (null != person.getBirthDate()) {
			caseSummaryData.setDob(person.getBirthDate()); // dob
		}

		return caseSummaryData;
	}

	@Override
	@Transactional
	@LoggingContext
	public CaseSummaryData findCaseSummaryDataByUscisReceiptNumber(String uscisReceiptNumber) {
		Case cisCase = caseDAO.findOneByUscisReceiptNumber(uscisReceiptNumber);
		long caseId = cisCase.getCaseIdentifier();
		if (cisCase.getBenefitRequest().getPerson() == null) {
			return getCaseSummary(caseId);
		}

		return getCaseSummary(caseId, getCaseApplicant(cisCase).getPartyIdentifier());
	}

	private Person getCaseApplicant(Case cisCase) {
		return cisCase.getApplicantForCase();
	}

	/**
	 * THIS METHOD WAS MOVED FROM CaseServiceImpl
	 *
	 * The purpose of this getCaseSummary() method is to retrieve data in order to
	 * display it on the floating menu. This data include the following fields: 1.
	 * Name - display full name 2. alias - this is an indicator that shows whether
	 * name is alias or legal 3. Alien Number 4. Account # 5. Date of Birth (DOB) 6.
	 * Country of Birth (COB) 7. Gender 8. Case State 9. Case Status 10. Case Sub
	 * Status
	 *
	 * @param caseId
	 * @param personId
	 *
	 */
	@Override
	@Transactional
	@LoggingContext({ @LoggingPoi(name = "caseId"), @LoggingPoi(index = 1, name = "personId") })
	public CaseSummaryData getCaseSummary(final long caseId, final long personId) {
		CaseSummaryData caseSummaryData = new CaseSummaryData();
		Person person = personDAO.findOne(personId);
		Case cisCase = caseDAO.findOne(caseId);
		String accommodationsRequested= null;
		
		if (person == null || cisCase == null) {
			return caseSummaryData;
		}

		if (doMultipleCasesExistForAccount(person)) {
			caseSummaryData.setMultipleCasesExisitForAccount(true);
		}

		/** Straight Up AssignmentHere **/
		getAssignment(cisCase, caseSummaryData);

		caseSummaryData.setCaseId(cisCase.getCaseIdentifier());
		caseSummaryData.setPersonId(personId);
		// Gather person names, alias, legal
		// Format first Name, Middle Name and Last Name as full name
		// Get Account Name
		getPersonNameAndImmigrationAccount(person, cisCase, caseSummaryData);

		getAlienNumberInfo(person, cisCase, caseSummaryData);

		PersonIdentification ssn = person.getPersonIdentificationByIdentificationType(IdentificationTypeCodeEnum.SOC_SEC_NMBR);
		caseSummaryData.setSsn(ssn!=null ? ssn.getPersonIdentValueText() : null);		

		PersonIdentification lcaOrEtaCaseNumber = person.getPersonIdentificationByIdentificationType(IdentificationTypeCodeEnum.LCA_ETA_CASE_NUMBER);
		caseSummaryData.setLcaOrEtaCaseNumber(lcaOrEtaCaseNumber!=null ? lcaOrEtaCaseNumber.getPersonIdentValueText() : null);
		
		caseSummaryData.setDob(person.getBirthDate()); // dob
		
		// When DOB is null, try to find it in other records. ELIS-129244
		if (person.getBirthDate() == null &&
			person.getAllPersonBirthDates() != null &&
			person.getAllPersonBirthDates().size() > 0) {
			  List<PersonBirthDate> birthDates = new ArrayList<>(person.getAllPersonBirthDates());
			  PersonBirthDate maxBirthDate = null;
			  for (PersonBirthDate pb : birthDates) {
				if (pb != null && pb.isPrimary() && !pb.isDeactivated() &&
					(maxBirthDate == null ||
				    (maxBirthDate.getRevisionNumber() < pb.getRevisionNumber()) ||
			    	(maxBirthDate.getRevisionNumber() == pb.getRevisionNumber() && maxBirthDate.getCreatedTimeStamp().compareTo(pb.getCreatedTimeStamp()) < 0))) {
					  maxBirthDate = pb;
				}
			}
			if (maxBirthDate != null) {
				caseSummaryData.setDob(maxBirthDate.getBirthDate());
			}

			logger.info("getCaseSummary - DOB: N/A, new DOB {}", caseSummaryData.getDob());
		}
		else if (person.getBirthDate() == null) {
			logger.info("getCaseSummary - DOB: N/A");
		}

		CasePersonSummaryData casePersonSummaryData = caseSummaryData.getCasePersonSummaryData();
		CasePersonBirthDate casePersonBirthDate = cisCase.getPrimaryCasePersonBirthDateForPerson(person);
		if (casePersonBirthDate != null) {
			casePersonSummaryData.setDob(casePersonBirthDate.getPersonBirthDate().getBirthDate());
		}

		// Country of Birth includes its country code and country description
		caseSummaryData.setCountryOfBirth(person.getBirthCountryCode());
		if (person.getBirthCountryCode() != null) {
			caseSummaryData.setCountryOfBirthDesc(
					birthCountryCodeDAO.findOne(person.getBirthCountryCode()).getBirthCountryName());
		}

		// Country of Citizenship includes its country code and country description
		if (null != person.getPrimaryCitizenship()) {
			if (null != person.getPrimaryCitizenship().getPersonCitizenshipId().getCountryCode()) {
				String coc = person.getPrimaryCitizenship().getPersonCitizenshipId().getCountryCode().getCountryCode();
				caseSummaryData.setCountryOfCitizenship(coc);
				caseSummaryData.setCountryOfCitizenshipDesc(countryCodeDAO.findOne(coc).getCountryName());
			}
		}
		CasePersonBiographics casePersonBiographics = cisCase.getCasePersonBiographicsForPerson(person);
		if (casePersonBiographics != null) {
			String birthCountryCd = casePersonBiographics.getBirthCountryCode();

			if (casePersonBiographics.getBirthCountryCode() != null) {
				casePersonSummaryData.setCountryOfBirth(birthCountryCd);
				casePersonSummaryData
						.setCountryOfBirthDesc(birthCountryCodeDAO.findOne(birthCountryCd).getBirthCountryName());
			}
			if (casePersonBiographics.getGenderCode() != null) {
				casePersonSummaryData.setGender(casePersonBiographics.getGenderCode().getGenderShortDesc());
			}
		}

		// Gender
		if (null != person.getGenderCode()) {
			caseSummaryData.setGender(person.getGenderCode().getGenderShortDesc());
		}

		// Receipt Date
		getReceiptAndSubmittedDate(cisCase, caseSummaryData);

		// StandAlone case
		caseSummaryData
				.setStandAlone(!benefitPacketService.isBenefitRequestActiveInPacket(cisCase.getCaseIdentifier()));

		// Case State / Case Status / Case Sub-status
		getCaseState(cisCase, caseSummaryData);
		
		// Form Type and Application Type
		BenefitTypeCode benefitTypeCode = cisCase.getBenefitRequest().getBenefitTypeCode();
		caseSummaryData.setFormType(benefitTypeCode.getBenefitCategoryCodeTbl().getFormNumber());
		caseSummaryData.setFormTypeForDisplay(BenefitTypeCodeEnum.getFormTypeForExternal(cisCase.getBenefitTypeCodeEnum()).getName());
		caseSummaryData.setApplicationType(benefitTypeCode.getBenefitTypeDescription());

		if (cisCase.getBenefitRequest().getPreparer() != null) {
			caseSummaryData.setPreparerPartyId(cisCase.getBenefitRequest().getPreparer().getPartyIdentifier());
		}

		setShowAFileTransferLink(cisCase, caseSummaryData);
		setShowAFileBypassLink(cisCase, caseSummaryData);

		// N-400-specific setters
		String formType = caseSummaryData.getFormType();
		if (isAllowableNForm(formType)) {
			setLatestInterviewAppt(caseSummaryData);
			setAFileTransferRequested(cisCase, caseSummaryData);
			setCurrentOfficerName(caseSummaryData);
			setDodDciiCheckResults(caseSummaryData.getCaseId(), caseSummaryData.getPersonId(), caseSummaryData,
					casePersonBiographicsDAO, dodDciiService);
		}
		
		boolean accommodationsNotEmpty = naturalizationService.isAccomodationRequested(cisCase);

		if (FormType.N400.getKeyId().equals(cisCase.getBenefitCategoryCode().getFormNumber())) {

			AccommodationInfo accInfo = accommodationService.getAccommodations(caseId);
			
			
			List<String> accomodationList = new ArrayList<String>();

			if (accInfo != null) {

				if (StringUtils.equals(accInfo.getDeafHardOfHearing(), YES.getLabel()))

				{

					accomodationList.add(HEARING_IMPAIRMENT);

				}

				if (StringUtils.equals(accInfo.getBlindSightImpaired(), YES.getLabel()))

				{

					accomodationList.add(VISUAL_IMPAIRMENT);

				}

				if (!StringUtils.isBlank(accInfo.getOtherDisabilityExplanation()) && 
						StringUtils.equals(accInfo.getOtherDisability(), YES.getLabel())
						&& !OTHER_DISABILITY_EXP.contains(accInfo.getOtherDisabilityExplanation().toUpperCase())
						) {

					accomodationList.add(OTHER_DISABILITY);

				}
			}

			StringBuilder accommodations = new StringBuilder();

			for (String accomodation : accomodationList) {

				accommodations.append(accomodation);
				accommodations.append(System.lineSeparator());

			}

			accommodationsRequested = accommodations.toString();

			if (StringUtils.isBlank(accommodationsRequested)) {

				accommodationsRequested = null != accInfo && StringUtils.equals(accInfo.getRequestAccommodation(), YES.getLabel()) 
						? ACCOMMODATIONS_REQUESTED : NOT_APPLICABLE;
			}

		}

		else {

			accommodationsRequested = accommodationsNotEmpty ? ACCOMMODATIONS_REQUESTED : NOT_APPLICABLE;
		}
		caseSummaryData.setAccommodationsRequested(accommodationsRequested);

		if (cisCase instanceof TPSCase) {
			TPSCase tpsCase = (TPSCase) cisCase;
			CaseTpsCountry caseTpsCountry = caseTpsCountryDAO.findByCisCaseCaseIdentifier(cisCase.getCaseIdentifier());
			if (caseTpsCountry != null) {
				caseSummaryData.setCaseType(caseTpsCountry.getTpsCountry().getTpsCountryGroupName());
				String declaredTPSCountry = caseTpsCountry.getDeclaredTPSCountry();
				if (StringUtils.isNotEmpty(declaredTPSCountry)) {
					caseSummaryData.setDeclaredTpsCountry(countryCodeDAO.findOne(declaredTPSCountry).getCountryName());
				}
				populateNCTCData(tpsCase.getUscisReceiptNumber(), caseSummaryData);
			} else {
				caseSummaryData.setCaseType(TpsCountryEnum.UNDETERMINED.getGroupName());
			}
			if (tpsCase.getCaseFilingTypeCode() != null) {
				caseSummaryData.setCaseFilingType(tpsCase.getCaseFilingTypeCode().getCaseFilingTypeDesc());
			}

		}
		
		if (cisCase instanceof I485Case && FilingCategoryEnum.FAMILY_BASED.getDescription().equals(cisCase.getCaseFilingTypeCode().getFilingCategoryCode().getFilingCateforyDesc())) {
			caseSummaryData.setCaseFilingType(FilingCategoryEnum.FAMILY_BASED.getDescription());
		}
		
		if (CaseFilingTypeEnum.I131_AOS_FILING == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(CaseFilingTypeEnum.I131_AOS_FILING.getDescription());
		}

		if (CaseFilingTypeEnum.I131_HP_FILING == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(CaseFilingTypeEnum.I131_HP_FILING.getDescription());
		}
		
		if (CaseFilingTypeEnum.I131_HP_ICE.equals(cisCase.getCaseFilingType())) {
			caseSummaryData.setCaseFilingType(cisCase.getCaseFilingType().getDescription());
		}
		
		if (CaseFilingTypeEnum.OAW_REPAROLE == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(CaseFilingTypeEnum.OAW_REPAROLE.getDescription());
		}
		
		if (CaseFilingTypeEnum.UHP_REPAROLE == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(CaseFilingTypeEnum.UHP_REPAROLE.getDescription());
		}
		
		if (CaseFilingTypeEnum.I131_DOS_LES_PAROLE == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(CaseFilingTypeEnum.I131_DOS_LES_PAROLE.getDescription());
		}
		
		if (CaseFilingTypeEnum.I131_CFRP_FILING == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(CaseFilingTypeEnum.I131_CFRP_FILING.getDescription());
		}
	
		if (CaseFilingTypeEnum.I131_DED == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(CaseFilingTypeEnum.I131_DED.getDescription());
		}
		
		if (CaseFilingTypeEnum.I131F == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(CaseFilingTypeEnum.I131F.getDescription());
		}
		
		if (CaseFilingTypeEnum.DED_EXTENTION_FILING == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(cisCase.getCaseFilingType().getDescription());
		}
		
		if (CaseFilingTypeEnum.DED_REPLACEMENT_FILING == cisCase.getCaseFilingType()) {
			caseSummaryData.setCaseFilingType(cisCase.getCaseFilingType().getDescription());
		}
		
		if (CaseFilingTypeEnum.FRTF_REPAROLE.equals(cisCase.getCaseFilingType())) {
			caseSummaryData.setCaseFilingType(cisCase.getCaseFilingType().getDescription());
		}
		
		// VAWA
		if (cisCase instanceof DACACase) {
			VAWAVerificationPayload payload = vawaCommonService.getVAWADecision(cisCase.getCaseIdentifier());
			if (payload.getDecision() != null) {
				caseSummaryData.setVAWACase(true);
				caseSummaryData.setVAWADecisionDesc(payload.getDecision());
			}
		}

		// Check if Case Type Has Been Assigned
		boolean caseTypeAssigned = benefitPacketService.packetActionExists(
				Arrays.asList(CaseActionTypeCodeGenericEnum.CASE_TYPE_RULES_EXPEDITION_COMPLETE),
				Arrays.asList(cisCase.getCaseIdentifier()));
		caseSummaryData.setCaseTypeAssigned(caseTypeAssigned);

		// 765 EAD/SimpleWorkflow
		populateI765Eligibility(cisCase, caseSummaryData);
		
		// I730 info
		if (cisCase.isI730()){
			populateI730Info(cisCase, caseSummaryData);
		}
		
		// I130 info
		if (cisCase instanceof I130Case){
			populateI130Info(cisCase, caseSummaryData);
		}
		
		// I485 Info
		if (cisCase instanceof I485Case) {
			populateI485Info(cisCase, caseSummaryData);
		}
		
		// I129 Info
		if (cisCase instanceof I129Case) {
			populateI129Info(cisCase, caseSummaryData);
		}
		
		// I945 Info
		if (FormType.I945.getName().equals(formType)) {
			populateI945Info(cisCase, caseSummaryData);
		}
		
		if (cisCase instanceof I290BCase) {
			populateI290BInfo(cisCase, caseSummaryData);
		}
		
		// I129H1B Info
		if (cisCase instanceof I129H1BCase) {
			populateI129H1BInfo(cisCase, caseSummaryData);
		}
		// I134 Info
		if (cisCase instanceof I134UHPCase || cisCase instanceof I134ATACase || cisCase instanceof I134AFRPCase) {
			populateI134Info(cisCase, caseSummaryData);
		}
		
		else if (cisCase instanceof G325RCase || FormType.I765.equals(cisCase.getFormType())) {
			setZNumberInfo(cisCase, caseSummaryData);
		}

		//I765 C09 Info
		if (CaseFilingTypeEnum.I765_C09_FILING.equals(cisCase.getCaseFilingType())) {
			populateI765C09Info(cisCase, caseSummaryData);
		}
		
		if(cisCase instanceof I539Case) {
			populateI539Info(cisCase, caseSummaryData);
		}
		populateI360Info(cisCase, caseSummaryData); //form type check is handled inside method!
		
		getCourtInformation(cisCase, caseSummaryData);
		
		return caseSummaryData;
	}

	/**
	 * Returns a summary of the case if petitioner is an organization. 
	 * 
	 * @param caseId
	 * @return CaseSummaryData
	 */
	@Override
	@Transactional
	@LoggingContext({ @LoggingPoi(name = "caseId")})
	public CaseSummaryData getCaseSummary(final long caseId) {
		CaseSummaryData caseSummaryData = new CaseSummaryData();
		Case cisCase = caseDAO.findOne(caseId);
		
		caseSummaryData.setCaseId(cisCase.getCaseIdentifier());
		caseSummaryData.setIsOrganization(true);
		
		// Assignment
		getAssignment(cisCase, caseSummaryData);
		
		// Receipt Date
		getReceiptAndSubmittedDate(cisCase, caseSummaryData);
		
		// Case State / Case Status / Case Sub-status
		getCaseState(cisCase, caseSummaryData);
		
		if (cisCase instanceof I129Case) {
			populateI129Info(cisCase, caseSummaryData);
		}
		if (cisCase instanceof I290BCase) {
			populateI290BInfo(cisCase, caseSummaryData);
		}
		if (cisCase instanceof I129H1BCase) {
			populateI129H1BInfo(cisCase, caseSummaryData);
		}
		if (cisCase instanceof I140Case) {
			populateI140Info(cisCase, caseSummaryData);
		}
		if (cisCase.isI539()) {
			populateI539Info(cisCase, caseSummaryData);
		}
		return caseSummaryData;
	}
	
	private void getCourtInformation(Case cisCase, CaseSummaryData caseSummaryData) {
		if (cisCase.isI862()) {
			CourtDTO courtDto = caseService.getCourtInformation(cisCase.getUscisReceiptNumber());
			caseSummaryData.setCourtDateAndTime(courtDto.getCourtDate());
			caseSummaryData.setCourtLocation(courtDto.getCourtLocation());
		}
	}
	
	private void getAssignment(Case cisCase, CaseSummaryData caseSummaryData) {
		setJurisdictionAndLocationInfo(cisCase, caseSummaryData);

		if (cisCase.getAssignedCisCaseLocation() != null) {
			caseSummaryData.setAssignedCisCaseLocationType(
					cisCase.getAssignedCisCaseLocation().getLocationType() == null ? null
							: new NameValuePair<>(
									cisCase.getAssignedCisCaseLocation().getLocationType()
											.getAlternateLocationTypeCode(),
									cisCase.getAssignedCisCaseLocation().getLocationType()
											.getLocationTypeDescription()));
		}

		caseSummaryData.setPhysicalAddress(LocationAssignmentServiceImpl
				.buildPhysicalAddress(LocationAssignmentServiceImpl.loadPrimaryAddress(cisCase.getBenefitRequest())));

		CaseAssignment caseAssignment = getCaseAssignment(cisCase);

		if (caseAssignment != null) {
			USCISEmployee uscisEmployee = caseAssignment.getUscisEmployee();
			if (uscisEmployee != null) {
				String assignedToName = getNameFromUscisEmployee(uscisEmployee);
				caseSummaryData.setCaseOwner(assignedToName);
				String assignedToSite = uscisEmployee.getIcamCisSite();
				if (org.apache.commons.lang3.StringUtils.isEmpty(assignedToSite)) {
					assignedToSite = NOT_APPLICABLE;
				}
				caseSummaryData.setCaseOwnerSite(assignedToSite);
			}
		} else {
			caseSummaryData.setCaseOwner("Not Assigned");
		}
	}

	private void setJurisdictionAndLocationInfo(Case cisCase, CaseSummaryData caseSummaryData) {
		caseSummaryData.setFcoJurisdiction(cisCase.getJurisdictionLocation() == null ? null
				: new NameValuePair<>(cisCase.getJurisdictionLocation().getLctnCd(),
						cisCase.getJurisdictionLocation().getLctnCdDesc()));
		
		caseSummaryData.setAdjudicatingFieldOfficeJurisdiction(cisCase.getAdjudicatingFcoLocationCode() == null ? null
				: new NameValuePair<>(cisCase.getAdjudicatingFcoLocationCode().getLctnCd(),
						cisCase.getAdjudicatingFcoLocationCode().getLctnCdDesc()));

		caseSummaryData.setServiceCenter(cisCase.getServiceCenter() == null ? null
				: new NameValuePair<>(cisCase.getServiceCenter().getLctnCd(),
						cisCase.getServiceCenter().getLctnCdDesc()));

		caseSummaryData.setAssignedCisCaseLocation(cisCase.getAssignedCisCaseLocation() == null ? null
				: new NameValuePair<>(cisCase.getAssignedCisCaseLocation().getLctnCd(),
						cisCase.getAssignedCisCaseLocation().getLctnCdDesc()));
	}
	
	private CaseAssignment getCaseAssignment(Case cisCase) {
		CaseAssignment caseAssignment = null;
		
		try {
			caseAssignment = caseAssignmentDAO.findOneByAssignmentActiveIndAndCisCase(ActiveIndicatorEnum.YES, cisCase);
		} catch (org.springframework.dao.IncorrectResultSizeDataAccessException irsdae) {
			List<CaseAssignment> caseAssignmentList = caseAssignmentDAO
					.findAllByAssignmentActiveIndAndCisCase(ActiveIndicatorEnum.YES, cisCase);
			if (CollectionUtils.isNotEmpty(caseAssignmentList)) {
				caseAssignment = caseAssignmentList.get(0);
			}
		}
		
		return caseAssignment;
	}

	private void getReceiptAndSubmittedDate(Case cisCase, CaseSummaryData caseSummaryData) {
		// Receipt Date
		if (null != cisCase.getBenefitRequest().getUscisReceiptDateTime()) {
			Timestamp receiptDate = cisCase.getBenefitRequest().getUscisReceiptDateTime();
			caseSummaryData.setReceiptDate(receiptDate); // dob
			caseSummaryData.setReceiptDateStr(dobDateFormatter.get().format(receiptDate));
		}

		// Application Submitted Date
		if (null != cisCase.getBenefitRequest().getApplicationSubmittedDate()) {
			java.util.Date applicationSubmittedDate = cisCase.getBenefitRequest().getApplicationSubmittedDate();

			caseSummaryData.setApplicationSubmittedDate(applicationSubmittedDate);
		}
	}
	
	private void getCaseState(Case cisCase, CaseSummaryData caseSummaryData) {
		// Get Packet Status for non StandAlone cases
		PacketStatus packetStatus = null;
		if (!caseSummaryData.isStandAlone()) {
			packetStatus = packetStatusDataService.getLatestPacketStatus(cisCase.getCaseIdentifier());
		}
		// Get Case State
		getCaseStateCode(cisCase, caseSummaryData, packetStatus);

		// Case Status
		getCaseStatusCode(cisCase, caseSummaryData, packetStatus);

		// Get Case Sub Status
		getCaseSubStatusCode(cisCase, caseSummaryData, packetStatus);
	}

	private void getCaseSubStatusCode(Case cisCase, CaseSummaryData caseSummaryData, PacketStatus packetStatus) {
		CaseSubstatusCode caseSubstatusCode = cisCase.getCaseSubstatusCode();
		if (caseSubstatusCode != null) {
			if (null != packetStatus && null != packetStatus.getCaseSubstatusCode()) {
				caseSummaryData.setCaseSubStatusCode(packetStatus.getCaseSubstatusCode().getCaseSubstatusCode());
				caseSummaryData
						.setCaseSubStatusCodeDesc(packetStatus.getCaseSubstatusCode().getCaseSubstatusCodeDesc());
			} else {
				caseSummaryData.setCaseSubStatusCode(caseSubstatusCode.getCaseSubstatusCode());
				caseSummaryData.setCaseSubStatusCodeDesc(caseSubstatusCode.getCaseSubstatusCodeDesc());
			}
		}
	}

	private void getCaseStatusCode(Case cisCase, CaseSummaryData caseSummaryData, PacketStatus packetStatus) {
		CaseStatusCode caseStatusCode = cisCase.getCaseStatusCode();
		if (null != caseStatusCode) {
			if (null != packetStatus && null != packetStatus.getCaseStatusCode()) {
				caseSummaryData.setCaseStatusCode(packetStatus.getCaseStatusCode().getCaseStatusCode());
				caseSummaryData.setCaseStatusCodeDesc(packetStatus.getCaseStatusCode().getCaseStatusCodeDesc());
			} else {
				caseSummaryData.setCaseStatusCode(caseStatusCode.getCaseStatusCode());
				caseSummaryData.setCaseStatusCodeDesc(caseStatusCode.getCaseStatusCodeDesc());
			}
		}
	}

	private void getCaseStateCode(Case cisCase, CaseSummaryData caseSummaryData, PacketStatus packetStatus) {
		CaseStateCode caseStateCode = cisCase.getCaseStateCode();
		if (null != caseStateCode) {
			if (null != packetStatus && null != packetStatus.getCaseStateCode()) {
				caseSummaryData.setCaseStateCode(packetStatus.getCaseStateCode().getCaseStateCode());
				caseSummaryData.setCaseStateCodeDesc(packetStatus.getCaseStateCode().getCaseStateCodeDesc());
			} else {
				caseSummaryData.setCaseStateCode(caseStateCode.getCaseStateCode());
				caseSummaryData.setCaseStateCodeDesc(caseStateCode.getCaseStateCodeDesc());
			}
		}
	}

	private void populateI485Info(Case cisCase, CaseSummaryData caseSummaryData) {
		
		
	}

	@LoggingContext({ @LoggingPoi(name = "caseId"), @LoggingPoi(index = 1, name = "applicantId") })
	public static void setDodDciiCheckResults(long caseId, long applicantId, CaseView caseView,
			CasePersonBiographicsDAO casePersonBiographicsDAO, DodDciiService dodDciiService) {
		boolean isMilitaryMember = getMilitaryMembership(caseId, applicantId, casePersonBiographicsDAO);
		caseView.setApplicantUsMilitaryMember(isMilitaryMember);
		if (isMilitaryMember) {
			String dciiSummary = dodDciiService.retrieveSummary(caseId);
			caseView.setDodDciiCheckResults(dciiSummary);
		}

	}

	@Override
	@Transactional
	@LoggingContext({ @LoggingPoi(name = "caseId"), @LoggingPoi(index = 1, name = "applicantId") })
	public boolean getMilitaryMembership(long caseId, long applicantId) {
		return getMilitaryMembership(caseId, applicantId, casePersonBiographicsDAO);
	}

	private static boolean getMilitaryMembership(long caseId, long applicantId,
			CasePersonBiographicsDAO casePersonBiographicsDAO) {
		List<CasePersonBiographics> applicantBiographics = casePersonBiographicsDAO
				.findByCisCaseCaseIdentifierAndPersonPartyIdentifier(caseId, applicantId);
		boolean isMilitaryMember = false;
		if (CollectionUtils.isNotEmpty(applicantBiographics)) {
			for (CasePersonBiographics applicantBiographic : applicantBiographics) {
				if (IndicatorEnum.YES.getValue().equals(applicantBiographic.getUsMilitaryMemberIndicator())) {
					isMilitaryMember = true;
				}
			}
		}
		return isMilitaryMember;
	}

	@Transactional(readOnly = true)
	@Override
	public boolean isApplicantAccompanyingMilitaryParent(final long cisCaseId) {
		return answeredYesToCaseQuestion(cisCaseId, SERVES_ARMED_FORCES_QUESTION)
				&& answeredYesToCaseQuestion(cisCaseId, ACCOMPANY_PARENT_QUESTION);
	}

	private boolean answeredYesToCaseQuestion(final long cisCaseId, final int questionId) {
		List<CaseQuestionResponse> responses = caseQuestionResponseDAO.findByCaseIdentifierAndQuestionId(cisCaseId,
				questionId);
		for (CaseQuestionResponse caseQuestionResponse : responses) {
			QuestionResponseIndicatorCode questionResponseIndicator = caseQuestionResponse
					.getQuestionResponseIndicator();
			if (questionResponseIndicator != null
					&& StringUtils.equalsIgnoreCase(YES.getValue(), questionResponseIndicator.getQuestnResponseIndicatorCd())) {
				return true;
			}
		}
		return false;
	}

	private void setCurrentOfficerName(CaseSummaryData caseSummaryData) {
		String currentOfficerName = employeeService.getNameFromIcamUUID(securityContextService.getCurrentUserId());
		caseSummaryData.setCurrentOfficerName(currentOfficerName);
	}

	private void setShowAFileTransferLink(Case cisCase, CaseSummaryData caseSummaryData) {
		if (FormType.N400.getName().equals(caseSummaryData.getFormType())
				|| FormType.N336.getName().equals(caseSummaryData.getFormType())) {
			UserTask requestAFileTask = workflowEventRepositoryService
					.findActiveTaskForCase(cisCase.getCaseIdentifier(), TaskType.REQUEST_AFILE);
			caseSummaryData.setShowAFileTransferLink(requestAFileTask == null);
		} else if (!FormType.I551.getName().equals(caseSummaryData.getFormType())) {
			caseSummaryData.setShowAFileTransferLink(true);
		}
	}

	private void setAFileTransferRequested(Case cisCase, CaseSummaryData caseSummaryData) {

		List<AFileTransferRequestInformation> transferRequests = transferRequestDAO
				.findByCaseIDAndTransferStatus(cisCase.getCaseIdentifier(), RequestAFileTransferStatus.IN_PROGRESS);
		boolean inProgress = false;
		if (transferRequests.size() > 0) {
			for (AFileTransferRequestInformation transferInfo : transferRequests) {
				RequestAFileTransferStatus status = transferInfo.getAfileTransferStatus();
				if (!inProgress) {
					inProgress = (status == RequestAFileTransferStatus.IN_PROGRESS
							|| status == RequestAFileTransferStatus.INITIATED);
					break;
				}
			}
		}
		caseSummaryData.setaFileTransferRequested(inProgress);
	}

	private void setShowAFileBypassLink(Case cisCase, CaseSummaryData caseSummaryData) {
		if (isAllowableNForm(caseSummaryData.getFormType()) || allowableIForms.contains(caseSummaryData.getFormType())) {
			List<UserTask> userTasks = workflowEventRepositoryService
					.findByCisCaseIdAndTaskType(cisCase.getCaseIdentifier(), TaskType.REQUEST_AFILE);
			boolean taskNonComplete = userTasks.stream()
					.anyMatch(task -> !TaskStatus.BYPASSED.equals(task.getTaskStatus())
							&& !TaskStatus.COMPLETED.equals(task.getTaskStatus()));
			caseSummaryData.setShowAFileBypassLink(taskNonComplete);
		}
	}

	private boolean doMultipleCasesExistForAccount(Party party) {
		if (party == null) {
			throw new BusinessException("Invalid party...");
		}

		List<RoleTypeCode> roleTypes = Arrays.asList(new RoleTypeCode(RoleTypeCodeEnum.APPLICANT),
				new RoleTypeCode(RoleTypeCodeEnum.PETITIONER),
				new RoleTypeCode(RoleTypeCodeEnum.PRINCIPAL_APPLICANT),
				new RoleTypeCode(RoleTypeCodeEnum.DERIVATIVE_APPLICANT));
		
		int cases = casePartyRoleDAO.findByPartyAndRoleTypeCodeIn(party, roleTypes).size();

		return cases > 1;
	}

	/**
	 * Get Person Name and Immigration Account
	 *
	 * @param personNames
	 * @param caseSummaryData
	 */
	@Override
	public void getPersonNameAndImmigrationAccount(Person person, Case cisCase, final CaseSummaryData caseSummaryData) {
		logger.info("Begin calling getPersonNameAndImmigrationAccount() method for person ID: {}", person.getKeyId());

		PersonName legalName = personNameDAO.findLegalName(person.getKeyId());

		if(null != legalName) {
			String fullLegalName = CaseDetailViewServiceImpl.getFullName(legalName);
			caseSummaryData.setName(fullLegalName);
			caseSummaryData.setNameLegalChange(NO.getLabel());

			if (Boolean.TRUE.equals(legalName.isNameLegallyChanged())) {
				caseSummaryData.setNameLegalChange(YES.getLabel());
			}
		}

		if (cisCase != null) {
			PersonName casePersonLegalName = cisCase.getCaseLevelPersonName(person, PersonNameUsageEnum.LEGAL, true);
			if (casePersonLegalName != null) {
				caseSummaryData.getCasePersonSummaryData()
						.setName(CaseDetailViewServiceImpl.getFullName(casePersonLegalName));
			}
		}

		PersonName alias = person.getName(PersonNameUsageEnum.ALIAS);
		if (alias != null && Boolean.FALSE.equals(alias.getManualDeleteIndicator())) {
			caseSummaryData.setAlias(CaseDetailViewServiceImpl.getFullName(alias));
		}

		// Immigration Account
		Person tempPerson = new Person(person.getKeyId());
		String accountNum = personToAccountNumber(tempPerson);
		caseSummaryData.setAccountNumber(accountNum);

		logger.info("End of calling getPersonNameAndImmigrationAccount() method.");

	}

	private void setFullNameOntoCaseSummaryData(final Set<PersonName> personNames,
			final CaseSummaryData caseSummaryData) {
		Iterator<PersonName> personNameIterator = personNames.iterator();
		while (personNameIterator.hasNext()) {
			PersonName personName = personNameIterator.next();
			String fullName = CaseDetailViewServiceImpl.getFullName(personName);

			if (isPrimaryName(personName)) {
				caseSummaryData.setName(fullName);
				if (BooleanUtils.isTrue(personName.isNameLegallyChanged())) {
					caseSummaryData.setNameLegalChange(YES.getLabel());
				} else {
					caseSummaryData.setNameLegalChange(NO.getLabel());
				}
				break;
			}
		}

	}

	private String getNameFromUscisEmployee(USCISEmployee uscisEmployee) {
		List<PersonName> personNames = personNameDAO
				.findLatestByPersonPartyIdentifier(uscisEmployee.getPartyIdentifier());
		if (personNames.size() > 0) {
			PersonName personName = personNames.get(0);
			StringBuilder employeeName = new StringBuilder(EMPTY);
			if (null != personName.getLastName()) {
				employeeName.append(personName.getLastName());
			}
			if (null != personName.getFirstName()) {
				employeeName.append(", " + personName.getFirstName());
			}
			return employeeName.toString();
		}
		return null;
	}

	private void getAlienNumberInfo(Person person, Case cisCase, final CaseSummaryData caseSummaryData) {
		AlienNumberDataWrapper aWrapper = alienNumberService.getAlienNumbers(person.getKeyId());
		caseSummaryData.setAlienRegistrationNumber(aWrapper.getPrimaryAlienNumber());
		CaseAlienNumber caseAlienNumber = cisCase.getPrimaryCaseAlienNumberForPerson(person);
		if (caseAlienNumber != null) {
			caseSummaryData.getCasePersonSummaryData()
					.setAlienRegistrationNumber(caseAlienNumber.getAlienNumber().getAlienNumber());
		}
	}
	

	/**
	 * @param immigrationAccounts
	 * @return
	 */
	private String getAccountNum(final Set<ImmigrationAccount> immigrationAccounts) {
		logger.info("Begin calling getAccountNum() method.");
		String accountNum = EMPTY;

		if (null != immigrationAccounts) {
			Iterator<ImmigrationAccount> immigrationIterator = immigrationAccounts.iterator();
			ImmigrationAccount immigrationAccount = null;

			while (immigrationIterator.hasNext()) {
				immigrationAccount = immigrationIterator.next();

				if (StringUtils.isNotBlank(immigrationAccount.getUscisImmigrationAccountId())) {
					accountNum = immigrationAccount.getUscisImmigrationAccountId();
				}

			}
		}

		logger.info("End of calling getAccountNum() method.");
		return accountNum;
	}

	private boolean isPrimaryName(final PersonName personName) {
		return personName != null && PersonNameUsageEnum.LEGAL == personName.getPersonNameUsageCode();
	}

	/*
	 * This method returns the latest interview appointment so that the interview
	 * date clock can be updated on the front end. Ideally, it would be better to
	 * just have a call updating the clock but there are issues with conversion
	 * between long and int to do the appropriate check.
	 */
	private void setLatestInterviewAppt(CaseSummaryData caseSummaryData) {
		Appointment latestAppointment = null;
		List<Appointment> completedAppointments = new ArrayList<>();
		Appointment appointment = null;
		
		String formType = caseSummaryData.getFormType();
		if (isAllowableNForm(formType)) {
			List<gov.dhs.uscis.elis2.shared.domain.interview.Appointment> appointments = appointmentDAO
					.findAllByCaseIdOrderByCreatedTimeStampDesc(caseSummaryData.getCaseId());

			// Get the latest interview appointment
			for (gov.dhs.uscis.elis2.shared.domain.interview.Appointment appt : appointments) {
				long apptTypeCode = appt.getAppointmentTypeCode().getAppointmentTypeCode();
				if (apptTypeCode == AppointmentTypeEnum.INTERVIEW.getValue()) {
					appointment = new Appointment();
					appointment.setAppointmentId(appt.getApptId());

					if (appt.getApptDt() != null) {
						appointment.setAppointmentDate(appt.getApptDt());
					}

					if (appt.getReservationId() != null) {
						appointment.setReservationId(appt.getReservationId());
					}

					if (appt.getAppointmentStatusCode() != null) {
						appointment
								.setAppointmentStatus((int) appt.getAppointmentStatusCode().getAppointmentStatusCode());
					}
					if (appt.getAppointmentTypeCode() != null) {
						appointment.setAppointmentType((int) appt.getAppointmentTypeCode().getAppointmentTypeCode());
					}

					if (appt.getLocationCode() != null) {
						appointment.setLocationCode(appt.getLocationCode().getLctnCd());
					}

					if (appt.getUpdatedTimeStamp() != null) {
						appointment.setLastUpdated(appt.getUpdatedTimeStamp());
					}
					
					if(appt.getApptDt() != null) {
						SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
						appointment.setAppointmentDateString(sdf.format(appt.getApptDt()));
					}
					
					if (latestAppointment == null || appt.getUpdatedTimeStamp().after(latestAppointment.getLastUpdated())) {
						latestAppointment = appointment;
					} 

					if (appt.getAppointmentStatusCode()
							.getAppointmentStatusCode() == AppointmentStatusCodeEnum.COMPLETED.getValue()) {
						completedAppointments.add(appointment);
					}
				}
			}
			
			// If "completed" interviews exist and the latest appointment is cancelled or
			// no_shoow or de_scheduled,
			// the latestAppointment is switched to the latest completed one.
			if (CollectionUtils.isNotEmpty(completedAppointments) && latestAppointment != null
					&& latestAppointment.getAppointmentType() == AppointmentTypeEnum.INTERVIEW.getValue()
					&& (latestAppointment.getAppointmentStatus() == AppointmentStatusCodeEnum.CANCELLED.getValue()
							|| latestAppointment.getAppointmentStatus() == AppointmentStatusCodeEnum.NO_SHOW.getValue()
							|| latestAppointment.getAppointmentStatus() == AppointmentStatusCodeEnum.DE_SCHEDULED
									.getValue())) {
				latestAppointment = completedAppointments.get(0);
			}
			caseSummaryData.setLatestInterviewAppt(latestAppointment);
		}
	}

	private void setSnapshotSignatures(Set<SnapshotSignature> signatures,
			SignatureDatePopulator signatureDatePopulator) {
		if (CollectionUtils.isNotEmpty(signatures)) {
			for (SnapshotSignature signature : signatures) {
				if (signature.getPreparerSignatureDate() != null
						&& StringUtils.isBlank(signatureDatePopulator.getPreparerDateOfSignature())) {
					signatureDatePopulator
							.setPreparerDateOfSignature(DateUtil.formatDate(signature.getPreparerSignatureDate()));
				}
				if (signature.getInterpreterSignatureDate() != null
						&& StringUtils.isBlank(signatureDatePopulator.getInterpreterDateOfSignature())) {
					signatureDatePopulator.setInterpreterDateOfSignature(
							DateUtil.formatDate(signature.getInterpreterSignatureDate()));
				}
			}
		}
	}

	private void populateBRSnapshotSignatureDates(BenefitRequest benefitRequest,
			SignatureDatePopulator signatureDatePopulator) {
		setSnapshotSignatures(benefitRequest.getSnapshotSignatures(), signatureDatePopulator);
	}

	@Override
	@Transactional(readOnly = true)
	public void getSnapshotSignatureDates(BenefitRequest benefitRequest,
			SignatureDatePopulator signatureDatePopulator) {
		populateBRSnapshotSignatureDates(benefitRequest, signatureDatePopulator);
		if (benefitRequest.getParty() != null && benefitRequest.getParty().getImmigrationAccount() != null) {
			List<SnapshotSignature> signatures = snapshotSignatureDAO
					.findByImmigrationAccount(benefitRequest.getParty().getImmigrationAccount());
			setSnapshotSignatures(new HashSet<>(signatures), signatureDatePopulator);
		}
	}

	private SnapshotSignature getSignatureByType(Set<SnapshotSignature> signatures, RoleTypeCodeEnum roleTypeCodeEnum) {
		SnapshotSignature retVal = null;
		if (CollectionUtils.isNotEmpty(signatures)) {
			for (SnapshotSignature signature : signatures) {
				if (signature.getPreparerSignatureDate() != null && RoleTypeCodeEnum.PREPARER == roleTypeCodeEnum) {
					retVal = signature;
				} else if (signature.getInterpreterSignatureDate() != null
						&& RoleTypeCodeEnum.INTERPRETER == roleTypeCodeEnum) {
					retVal = signature;
				}
			}
		}
		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public SnapshotSignature getSnapshotSignature(BenefitRequest benefitRequest, RoleTypeCodeEnum roleTypeCodeEnum) {
		SnapshotSignature retVal;
		SnapshotSignature signature = getSignatureByType(benefitRequest.getSnapshotSignatures(), roleTypeCodeEnum);
		SnapshotSignature signature2 = null;
		if (benefitRequest.getParty() != null && benefitRequest.getParty().getImmigrationAccount() != null) {
			List<SnapshotSignature> signatures = snapshotSignatureDAO
					.findByImmigrationAccount(benefitRequest.getParty().getImmigrationAccount());
			signature2 = getSignatureByType(new HashSet<>(signatures), roleTypeCodeEnum);
		}
		retVal = signature2 == null ? signature : signature2;

		return retVal;
	}

	protected boolean isAllowableNForm(String formType) {
		boolean allowableNForm = allowableNForms.contains(formType);
		return allowableNForm;
	}

	private void populateNCTCData(String uscisReceiptNumber, CaseSummaryData caseSummaryData) {
		BenefitPacket primary = benefitPacketService.findPrimaryBenefitPacket(uscisReceiptNumber);
		if (primary != null) {
			TPSCase primaryCase = (TPSCase) primary.getCisCase();
			CaseTpsCountry caseTpsCountry = caseTpsCountryDAO
					.findByCisCaseCaseIdentifier(primaryCase.getCaseIdentifier());
			if (Boolean.TRUE.equals(caseTpsCountry.getTpsCountry().getNctcCheckRequiredIndicator()) 
					&& primaryCase.isApplicantOfAge(CommonServicesConstants.NCTC_CHECK_MIN_AGE)) {
				caseSummaryData.setNctcConcern(true);
				caseSummaryData.setNctcState(caseTpsCountry.getNctcStateCodeEnum() != null
						? caseTpsCountry.getNctcStateCodeEnum().getDescription()
						: StringUtils.EMPTY);
				NCTCResultCodeEnum nctcResult = nctcService
						.getNCTCCodeEnumForPacket(primaryCase.getUscisReceiptNumber());
				caseSummaryData.setNctcResult(nctcResult == null ? null : nctcResult.getDescription());
				caseSummaryData.setNctcReferralCompleted(NCTCResultCodeEnum.NCTC_HIT.equals(nctcResult)
						? nctcService.isNCTCHitReviewed(primaryCase.getUscisReceiptNumber())
						: false);
			}
		}
	}

	private void populateI765Eligibility(Case cisCase, CaseSummaryData caseSummaryData) {
		if (cisCase.getFormType().equals(FormType.I765)) {
			EadEligibilityCategoryCodeEnum eadCode = caseService.getEmploymentEligibilityFromCase(cisCase);
			
			Collection<CaseEligibilityEmployment> caseEligibilityEmploymentSet = caseEligibilityEmploymentDAO
					.findAllByCaseId(cisCase.getCaseIdentifier());
			CaseEligibilityEmployment maxCaseEligibilityEmployment = null;
			if(CollectionUtils.isNotEmpty(caseEligibilityEmploymentSet)) {
				maxCaseEligibilityEmployment = Collections.max(caseEligibilityEmploymentSet,
						(a, b) -> new CompareToBuilder().append(a.getRevisionNumber(), b.getRevisionNumber()).build());
			}
			
			//Temporary to deal with difference in casing
			if (maxCaseEligibilityEmployment != null && StringUtils.equalsIgnoreCase(EadEligibilityCategoryCodeEnum.C11A.getName(), maxCaseEligibilityEmployment.getResultText())) {
				eadCode = EadEligibilityCategoryCodeEnum.C11A;
			} else if (maxCaseEligibilityEmployment != null && StringUtils.equalsIgnoreCase(EadEligibilityCategoryCodeEnum.C11B.getName(), maxCaseEligibilityEmployment.getResultText())) {
				eadCode = EadEligibilityCategoryCodeEnum.C11B;
			} 
			
			/*List<String> c11EadNames = Arrays.asList(
					EadEligibilityCategoryCodeEnum.C11A.getName(), 
					EadEligibilityCategoryCodeEnum.C11B.getName());
			if (ce != null && c11EadNames.contains(ce.getResultText())) {
				eadCode = EadEligibilityCategoryCodeEnum.parse(ce.getResultText());
			}*/
			
			caseSummaryData.setEadCode(eadCode.getCode());
		}
	}
	
	private void populateI730Info(Case cisCase, CaseSummaryData caseSummaryData) {
		CasePartyRole beneficiaryCasePartyRole = cisCase.getCasePartyRoleByRoleTypeCode(RoleTypeCodeEnum.BENEFICIARY);
		Person beneficiaryPerson = beneficiaryCasePartyRole.getPerson();
		AlienNumberDataWrapper aWrapper = alienNumberService.getAlienNumbers(beneficiaryPerson.getKeyId());
		caseSummaryData.setBeneficiaryAlienRegistrationNumber(aWrapper.getPrimaryAlienNumber());
		caseSummaryData.setBeneficiaryDob(beneficiaryPerson.getBirthDate());
		caseSummaryData.setBeneficiaryName(beneficiaryPerson.getLegalName().getFullName());
	}
	
	private void populateI130Info(Case cisCase, CaseSummaryData caseSummaryData) {
		setShowPriorityDateChange(cisCase, caseSummaryData);
		setPriorityDate(cisCase, caseSummaryData);
		CasePartyRole beneficiaryCasePartyRole = cisCase.getCasePartyRoleByRoleTypeCode(RoleTypeCodeEnum.BENEFICIARY);
		Person beneficiaryPerson = beneficiaryCasePartyRole.getPerson();
		AlienNumberDataWrapper aWrapper = alienNumberService.getAlienNumbers(beneficiaryPerson.getKeyId());
		caseSummaryData.setBeneficiaryAlienRegistrationNumber(aWrapper.getPrimaryAlienNumber());
		caseSummaryData.setBeneficiaryDob(beneficiaryPerson.getBirthDate());
		caseSummaryData.setBeneficiaryName(beneficiaryPerson.getLegalName().getFullName());
	}
	
	private void setShowPriorityDateChange(Case cisCase, CaseSummaryData caseSummaryData) {
		UserTask priorityDateChangeTask = workflowEventRepositoryService
				.findActiveTaskForCase(cisCase.getCaseIdentifier(), TaskType.PRIORITY_DATE_CHANGE);
		caseSummaryData.setShowPriorityDateChange(priorityDateChangeTask == null);
	}
	
	private void setPriorityDate(Case cisCase, CaseSummaryData caseSummaryData) {
		CasePriorityDate mostRecentCPD = casePriorityDateDAO.findByCaseIdDescMaxRevision(cisCase.getCaseIdentifier());
		if (mostRecentCPD != null) {
			caseSummaryData.setCasePriorityDate(mostRecentCPD.getPriorityDate());
		}
	}
	
	private void populateI360Info(Case cisCase, CaseSummaryData caseSummaryData) {
		if(cisCase.getFormType().equals(FormType.I360)) {
			setShowPriorityDateChange(cisCase, caseSummaryData);
			setPriorityDate(cisCase, caseSummaryData);	
		}
	}
	
	private void populateI129Info(Case cisCase, CaseSummaryData caseSummaryData) {
		I129CaseDetailView caseDetailView = (I129CaseDetailView) getCaseDetail(cisCase.getCaseIdentifier());
		caseSummaryData.setFein(caseDetailView.getFein());
		caseSummaryData.setCompanyName(caseDetailView.getCompanyName());
		caseSummaryData.setLcaOrEtaCaseNumber(caseDetailView.getLcaOrEtaCaseNumber());
		caseSummaryData.setTotalNumberOfWorkers(caseDetailView.getTotalNumberOfWorkers());
		caseSummaryData.setPreparerOrgName(caseDetailView.getPreparerOrgName());
		caseSummaryData.setPreparerEmail(caseDetailView.getPreparerEmail());
		caseSummaryData.setRequestedNonImmigrantClassfication(caseDetailView.getRequestedNonImmigrantClassfication());
		Party party = cisCase.getApplicantPartyForCase();
		Email email = party.getPrimaryEmail(EmailType.OTHER);
		Phone dayTimePhone = party.getPrimaryPhone(PhoneTypeCode.DAYTIME);
		caseSummaryData.setContactEmail(email!=null ? email.getEmailAddress() : null);
		caseSummaryData.setContactPhoneNumber(dayTimePhone!=null ? dayTimePhone.getPhoneNumber() : null);
	}
	private void populateI129H1BInfo(Case cisCase, CaseSummaryData caseSummaryData) {
		CasePersonSummaryData beneficiaryDto = caseSummaryData.getBeneficiaryData();
		CasePartyRole beneficiaryCasePartyRole = cisCase.getCasePartyRoleByRoleTypeCode(RoleTypeCodeEnum.BENEFICIARY);
		
		if(beneficiaryCasePartyRole != null && beneficiaryCasePartyRole.getPerson() != null) {
		Person beneficiaryPerson = beneficiaryCasePartyRole.getPerson();
		beneficiaryDto.setNameInfo(beneficiaryPerson);
		}
		caseSummaryData.setCasePersonSummaryData(beneficiaryDto);
		
	}
	
	private void populateI140Info(Case cisCase, CaseSummaryData caseSummaryData) {
		I140CaseDetailView caseDetailView = (I140CaseDetailView) getCaseDetail(cisCase.getCaseIdentifier());
		caseSummaryData.setFein(caseDetailView.getFein());
		caseSummaryData.setCompanyName(caseDetailView.getCompanyName());
	}
	
	private void populateI134Info(Case cisCase, CaseSummaryData caseSummaryData) {
		CasePersonSummaryData beneficiaryDto = caseSummaryData.getBeneficiaryData();
		CasePartyRole beneficiaryCasePartyRole = cisCase.getCasePartyRoleByRoleTypeCode(RoleTypeCodeEnum.BENEFICIARY);
		
		if(beneficiaryCasePartyRole != null && beneficiaryCasePartyRole.getPerson() != null) {
			Person beneficiaryPerson = beneficiaryCasePartyRole.getPerson();
			beneficiaryDto.setNameInfo(beneficiaryPerson);
		}
		caseSummaryData.setCasePersonSummaryData(beneficiaryDto);
		caseSummaryData.setCaseFilingType(cisCase.getCaseFilingType().getDescription());
		
		setZNumberInfo(cisCase, caseSummaryData);
	}
	
	private void populateI539Info(Case cisCase, CaseSummaryData caseSummaryData) {

		I539CaseDetailView caseDetailView = (I539CaseDetailView) getCaseDetail(cisCase.getCaseIdentifier());
		
		caseSummaryData.setCurrentNonImmigrantClassification(caseDetailView.getCurrentNonImmigrationStatus());
		caseSummaryData.setRequestedNonImmigrantClassfication(caseDetailView.getRequestedNonImmigrantClassfication());
	}

	protected void setZNumberInfo(Case cisCase, CaseSummaryData caseSummaryData) {
		PersonIdentification accountZNumber = personIdentificationService.
				getLatestActiveByPartyAndType(cisCase.getApplicantForCase().getPartyId(), IdentificationTypeCodeEnum.ZNUMBER);
		
		PersonIdentification caseZNumber = personIdentificationService.
				getLatestActiveByCaseAndType(cisCase.getKeyId(), IdentificationTypeCodeEnum.ZNUMBER);
		
		caseSummaryData.setzNumberAccount(accountZNumber != null ? accountZNumber.getPersonIdentValueText() : null);
		caseSummaryData.setzNumberCase(caseZNumber != null ? caseZNumber.getPersonIdentValueText() : null);
	}
	
	private void populateI945Info(Case cisCase, CaseSummaryData caseSummaryData) {
		CasePersonSummaryData beneficiaryDto = caseSummaryData.getBeneficiaryData();
		CasePartyRole beneficiaryCasePartyRole = cisCase.getCasePartyRoleByRoleTypeCode(RoleTypeCodeEnum.BENEFICIARY);
		
		if(beneficiaryCasePartyRole != null && beneficiaryCasePartyRole.getPerson() != null) {
			Person beneficiaryPerson = beneficiaryCasePartyRole.getPerson();
			beneficiaryDto.setNameInfo(beneficiaryPerson);
			
			// Country of Birth includes its country code and country description
			if (beneficiaryPerson.getBirthCountryCode() != null) {
				beneficiaryDto.setCountryOfBirthDesc(
						birthCountryCodeDAO.findOne(beneficiaryPerson.getBirthCountryCode()).getBirthCountryName());
			}

			// Immigration Account
			String accountNum = personToAccountNumber(beneficiaryPerson);
			beneficiaryDto.setAccountNumber(accountNum);
		}
		
		bondStatusDAO.findAllByCaseIdDesc(cisCase.getCaseIdentifier()).stream().findFirst()
			.ifPresent(s -> caseSummaryData.setBondStatus(BondStatusCodeEnum.parse(s.getBondStatusCd())));
		
		caseSummaryData.setShowBondMonitoring(canInitI945BondMonitoring(cisCase));	
	}
	
	private void populateI765C09Info(Case cisCase, CaseSummaryData caseSummaryData) {
		Optional<CaseFlagIndicator> applicationTypeFlag = caseFlagIndicatorDAO.findActiveFlagsByCaseIdAndFlagTypeCodeIn(
				cisCase.getCaseIdentifier(), ImmutableList.copyOf(c09FlagToApplicationType.keySet())).stream().findFirst();
		if (applicationTypeFlag.isPresent() && applicationTypeFlag.get().getFlagTypeCode() != null) {
			I765C09ApplicationTypeEnum appType = c09FlagToApplicationType.get(applicationTypeFlag.get().getFlagTypeCode().getFlagTypeCode());
			if (appType != null) {
				caseSummaryData.setRelatedApplicationType(appType);
			}
		}
		
		Optional<CaseFlagIndicator> filingCategoryFlag = caseFlagIndicatorDAO.findActiveFlagsByCaseIdAndFlagTypeCodeIn(
				cisCase.getCaseIdentifier(), ImmutableList.copyOf(c09FlagToFilingCategory.keySet())).stream().findFirst();
		if (filingCategoryFlag.isPresent() && filingCategoryFlag.get().getFlagTypeCode() != null) {
			I765C09FilingCategoryEnum appType = c09FlagToFilingCategory.get(filingCategoryFlag.get().getFlagTypeCode().getFlagTypeCode());
			if (appType != null) {
				caseSummaryData.setRelatedFilingCategory(appType);
			}
		}
	}
	
	private boolean canInitI945BondMonitoring(Case cisCase) {
		if(isCaseClosed(cisCase)) {
			return false;
		}
		
		UserTask bondMonitoringTask = workflowEventRepositoryService.findActiveTaskForCase(cisCase.getCaseIdentifier(), TaskType.BOND_MONITORING);
		boolean noTask = bondMonitoringTask == null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomSecurityExpressionRoot csr = new CustomSecurityExpressionRoot(authentication);
		boolean canEdit = csr.isActionAllowed("bondMonitoring", "edit");

		return noTask && canEdit;
	}

	private boolean isCaseClosed(Case cisCase) {
		return cisCase.getCaseStateCode() != null && CaseStateCodeEnum.CLOSED.equals(cisCase.getCaseStateCodeValue());
	}
	
	private void populateI290BInfo(Case cisCase, CaseSummaryData caseSummaryData) {
		I290BCaseDetailView caseDetailView = (I290BCaseDetailView) getCaseDetail(cisCase.getCaseIdentifier());
		caseSummaryData.setCompanyName(caseDetailView.getCompanyName());
		caseSummaryData.setPreparerOrgName(caseDetailView.getPreparerOrgName());
		caseSummaryData.setPreparerEmail(caseDetailView.getPreparerEmail());
		Party party = cisCase.getApplicantPartyForCase();
		Email email = party.getPrimaryEmail(EmailType.OTHER);
		Phone dayTimePhone = party.getPrimaryPhone(PhoneTypeCode.DAYTIME);
		caseSummaryData.setContactEmail(email!=null ? email.getEmailAddress() : null);
		caseSummaryData.setContactPhoneNumber(dayTimePhone!=null ? dayTimePhone.getPhoneNumber() : null);
	}

	private String personToAccountNumber(Person person) {
		Set<ImmigrationAccount> immigrationAccounts = new HashSet<>(immigrationAccountDAO.findByParty(person));
		return getAccountNum(immigrationAccounts);
	}

	@Override
	@Transactional
	public void updateSSAQuestions(long caseId, SocialSecurityQuestionsDTO ssaQuestions) {
		Case cisCase = caseDAO.findByCaseIdentifier(caseId);
		
		//Update SSN
		List<CasePersonIdentification> currSSNCpiList = casePersonIdentificationDAO.findOneSSNByCaseIDMaxRevision(caseId);
		CasePersonIdentification currSSNCpi = CollectionUtils.isNotEmpty(currSSNCpiList) ? currSSNCpiList.get(0) : null;
		String newSSN = ssaQuestions.getSsn();
		if (newSSN != null && newSSN.length() != 0) {
			newSSN = newSSN.substring(0, 3) + newSSN.substring(4, 6) + newSSN.substring(7, 11);
			if (currSSNCpi != null) {
				PersonIdentification currPersIdent = currSSNCpi.getPersonIdentification();
				if (!currPersIdent.getPersonIdentValueText().equals(newSSN)) {
					PersonIdentification newPersIdent = new PersonIdentification(currPersIdent);
					newPersIdent.setRevisionNumber(currPersIdent.getRevisionNumber() + 1);
					newPersIdent.setRelatedId(currPersIdent.getKeyId());
					newPersIdent.setPersonIdentValueText(newSSN);
					CasePersonIdentification newSSNCpi = new CasePersonIdentification(cisCase, newPersIdent);
					newSSNCpi.setRevisionNumber(currSSNCpi.getRevisionNumber() + 1);
					newSSNCpi.setRelatedId(currSSNCpi.getKeyId());
					personIdentificationDAO.saveAndFlush(newPersIdent);
					casePersonIdentificationDAO.saveAndFlush(newSSNCpi);
				}
			} else {
				PersonIdentification newPersIdent = new PersonIdentification(IdentificationTypeCodeEnum.SOC_SEC_NMBR.getValue(), newSSN);
				newPersIdent.setParty(cisCase.getApplicantPartyForCase());
				newPersIdent.setPersonIdentValueText(newSSN);
				CasePersonIdentification newSSNCpi = new CasePersonIdentification(cisCase, newPersIdent);
				personIdentificationDAO.saveAndFlush(newPersIdent);
				casePersonIdentificationDAO.saveAndFlush(newSSNCpi);
			}
		}
		
		//Update SSA questions
		List<FormQuestionEnum> ssaQuestionEnums = SSN_QUESTIONS_ENUMS_I485;
		if (FormType.I765.equals(cisCase.getFormType())) {
			ssaQuestionEnums = SSN_QUESTIONS_ENUMS_I765;
		}
		if (FormType.G325A.equals(cisCase.getFormType())){
			ssaQuestionEnums = SSN_QUESTIONS_ENUMS_G325A;
		}
		for (FormQuestionEnum questionEnum : ssaQuestionEnums) {
			CaseQuestionResponse currCqr = getCaseQuestionResponse(cisCase, questionEnum);
			Boolean newResponse = null;
			switch (questionEnum) {
				case I485_EVER_OFFICIALLY_ISSUED_SOCIAL_SECURITY_CARD:
				case EVER_ISSUED_SOCIAL_SECURITY_NUMBER:
					newResponse = ssaQuestions.getSsnEverIssued();
					break;
				case I485_WANTS_SSA_TO_ISSUE_SOCIAL_SECURITY_CARD:
				case REQUESTING_SOCIAL_SECURITY_NUMBER:
					newResponse = ssaQuestions.getRequestSSN();
					break;
				case I485_HAS_CONSENT_FOR_DISCLOSURE:
				case CONSENT_FOR_DISCLOSURE_SSA:
					newResponse = ssaQuestions.getConsentForDisclosure();
					break;
				default:
					break;
			}
			String newResponseStr = CaseQuestionResponseIndicatorEnum.parseBoolean(newResponse).getValue();
			String currResponseStr = "";
			if (currCqr != null) {
				currResponseStr = currCqr.getQuestionResponseIndicator().getQuestnResponseIndicatorCd();
			}
			
			if (!newResponseStr.equals(currResponseStr)) {
				CaseQuestionResponse newCqr;
				if (currCqr != null) {
					newCqr = CaseQuestionResponseBuilder.builder()
							.withCase(cisCase)
							.withFormQuestion(currCqr.getFormQuestion())
							.withQuestionResponseIndicatorCode(newResponseStr)
							.withRevisionNumber(currCqr.getRevisionNumber() + 1)
							.build();
					newCqr.setRelatedId(currCqr.getKeyId());
				} else {
					newCqr = CaseQuestionResponseBuilder.builder()
							.withCase(cisCase)
							.withFormQuestion(new FormQuestion(questionEnum, cisCase.getFormRevision()))
							.withQuestionResponseIndicatorCode(newResponseStr)
							.build();
				}
				WorksheetStatus wkstTrckHist = new WorksheetStatus(UiHeaderCodeEnum.SSA_QUESTIONS);
				wkstTrckHist.setHeaderSubSection(uiHeaderSubSectionDAO.findByheaderAndUiHeaderSubsectionCode(
						worksheetHeaderDAO.findOne(UiHeaderCodeEnum.SSA_QUESTIONS.getId()),
						formQuestionToUiHeaderSubSection.get(questionEnum)));
				wkstTrckHist.setCisCase(cisCase);
				wkstTrckHist.setCaseQuestionResponse(newCqr);
				wkstTrckHist.setUiQuestionResponseIndicator(CaseQuestionResponseIndicatorEnum.parse(newResponseStr));
				
				caseQuestionResponseDAO.saveAndFlush(newCqr);
				worksheetStatusDAO.saveAndFlush(wkstTrckHist);
			}
		}
		
	}
	
	private CaseQuestionResponse getCaseQuestionResponse(Case cisCase, FormQuestionEnum questionEnum) {
		Optional<Integer> questionId = questionEnum.getQuestionIdNullSafe(cisCase.getBenefitRequest().getRevision());
		if (questionId.isPresent()) {
			Integer id = questionId.get();
			return caseQuestionResponseDAO.findOneByCaseIdentifierAndQuestionId(cisCase.getCaseIdentifier(), id);
		}
		return null;
	}

	@Override
	public void updateC09FilingTypeAndCategory(long caseId, String applicationType, String filingCategory) {
		if (applicationType == null) {
			//error?
			return;
		}
		//remove existing flags
		Optional<CaseFlagIndicator> applicationTypeFlag = caseFlagIndicatorDAO.findActiveFlagsByCaseIdAndFlagTypeCodeIn(
				caseId, ImmutableList.copyOf(c09FlagToApplicationType.keySet())).stream().findFirst();
		if (applicationTypeFlag.isPresent()) {
			caseFlagsService.setFlagToInactive(caseId, applicationTypeFlag.get().getFlagTypeCode().getFlagTypeCode());
		}
		
		Optional<CaseFlagIndicator> filingCategoryFlag = caseFlagIndicatorDAO.findActiveFlagsByCaseIdAndFlagTypeCodeIn(
				caseId, ImmutableList.copyOf(c09FlagToFilingCategory.keySet())).stream().findFirst();
		if (filingCategoryFlag.isPresent()) {
			caseFlagsService.setFlagToInactive(caseId, filingCategoryFlag.get().getFlagTypeCode().getFlagTypeCode());
		}
		
		//Adding new flags
		Integer flagTypCd = null;
		for (Entry<Integer, I765C09ApplicationTypeEnum> entry : c09FlagToApplicationType.entrySet()) {
			if (applicationType.equals(entry.getValue().getValue())) {
				flagTypCd = entry.getKey();
				break;
			}
		}
		if (flagTypCd != null) {
			FlagTypeCode flagTypeCode = flagTypeCodeDAO.findOne(flagTypCd);
			caseFlagsService.addFlag(caseId, flagTypeCode);
		}
		flagTypCd = null;
		for (Entry<Integer, I765C09FilingCategoryEnum> entry : c09FlagToFilingCategory.entrySet()) {
			if (filingCategory.equals(entry.getValue().getValue())) {
				flagTypCd = entry.getKey();
				break;
			}
		}
		if (flagTypCd != null) {
			FlagTypeCode flagTypeCode = flagTypeCodeDAO.findOne(flagTypCd);
			caseFlagsService.addFlag(caseId, flagTypeCode);
		}
		caseActionHandler.newCaseActionHistory(caseId, CaseActionTypeCodeGenericEnum.FILING_TYPE_CHANGED);
		
	}
	
	@Override
	public boolean isC09EmploymentBasedCase(long caseId) {
		return !caseFlagIndicatorDAO.findActiveFlagsByCaseIdAndFlagTypeCodeIn(
				caseId, Arrays.asList(SQAFlagEnum.I485_EMPLOYMENT_BASED.value())).isEmpty();
	}
	
	@Override
	@Transactional
	public boolean shouldPreventI485EbForward(long caseId) {
		Case cisCase = caseDAO.findByCaseIdentifier(caseId);
		if (null != cisCase && cisCase.isI485EB()) {
			return !underlyingPetitionIsApproved(cisCase);
		}
		return false;
	}
	
	private boolean underlyingPetitionIsApproved(Case cisCase) {
		List<RelatedBenefitRequest> rbrs = relatedBenefitRequestService.getLatestRBRUnderlyingPetitionsByMainReceiptNumber(cisCase.getUscisReceiptNumber());
		if (!rbrs.isEmpty()) {
			RelatedBenefitRequest underlyingPetition = rbrs.get(0);
			Object upObject = null;
			try {
				upObject = linkingPriorCaseHelper.getCaseTypeObjectFromElisOrExternal(underlyingPetition.getUscisReceiptNumberToUse());
			} catch (SystemException e) {
				logger.info("Error while querying PCQS for {} with underlying receipt {} : {}", cisCase.getUscisReceiptNumber(), underlyingPetition.getUscisReceiptNumberToUse(), e.getMessage());
			}
			
			if (null != upObject) {
				return underlyingPetitionHasApprovalDecision(upObject);
			}
		}
		return false;
	}

	private boolean underlyingPetitionHasApprovalDecision(Object upObject) {
		if (upObject instanceof Case) {
			CaseDec finalizedDec = ((Case) upObject).getFinalizedCaseDec();
			if (null != finalizedDec) {
				return CaseDecisionCdEnum.APPROVED.equals(finalizedDec.getCaseDecisionCode());
			}
		} else if (upObject instanceof PriorCaseDTO) {
			CaseStatusDTO c3Status = ((PriorCaseDTO) upObject).getCaseStatus();
			if (null != c3Status) {
				return CaseStatusCodeEnum.APPROVALS.contains(c3Status.getCaseStatus());
			}
		}
		return false;
	}

	public void updateFilingCategory(long caseId, CaseFilingCategoryDTO filingCategoryDTO) {
		if (StringUtils.isNotBlank(filingCategoryDTO.getCaseFilingTypeCode())) {
			CaseFilingTypeEnum cft = CaseFilingTypeEnum.parse(Integer.parseInt(filingCategoryDTO.getCaseFilingTypeCode()));
			
			Case cisCase = caseDAO.findOne(caseId);
			cisCase.setCaseFilingType(cft);
			
			BenefitRequest br = cisCase.getBenefitRequest();
			updateBenefitTypeCode(br, cft);
			
			LocationCode serviceCenter = locationAssignmentService.assignServiceCenter(br);
			if (serviceCenter != null) {
				assignCaseLocationTo(cisCase, serviceCenter);
			}
			caseDAO.save(cisCase);
			
			caseActionHandler.newCaseActionHistory(caseId, CaseActionTypeCodeGenericEnum.ELIGIBILITY_REASON_UPDATED);
		}
	}

	private void updateBenefitTypeCode(BenefitRequest br, CaseFilingTypeEnum cft) {
		Map<CaseFilingTypeEnum, BenefitTypeCodeEnum> benefitTypeCodeMap = ImmutableMap.of(
				CaseFilingTypeEnum.I131_RTD_LPR_INSIDE_US, BenefitTypeCodeEnum.RESIDENT_REFUGEE_TRAVEL_INSIDE_US,
				CaseFilingTypeEnum.I131_RTD_NONLPR_INSIDE_US, BenefitTypeCodeEnum.REFUGEE_TRAVEL_INSIDE_US,
				CaseFilingTypeEnum.I131_RTD_LPR_OUTSIDE_US, BenefitTypeCodeEnum.RESIDENT_REFUGEE_TRAVEL_OUTSIDE_US,
				CaseFilingTypeEnum.I131_RTD_NONLPR_OUTSIDE_US, BenefitTypeCodeEnum.REFUGEE_TRAVEL_OUTSIDE_US);

		Optional.ofNullable(benefitTypeCodeMap.get(cft))
									.map(btcEnum -> benefitTypeCodeDAO.findOne(btcEnum.getValue()))
									.ifPresent(btc -> {
										br.setBenefitTypeCode(btc);
										benefitRequestDAO.save(br);
									});
	}
	
	private void assignCaseLocationTo(Case cisCase, LocationCode location) {
		logger.info("Assigning Service Center and Current Location to {} for {}", location.getLctnCd(), cisCase.getUscisReceiptNumber());
		LocationCode currentCaseLocation = location;
		cisCase.setServiceCenter(location);

		if (cisCase.getBenefitRequest().getSourceType() == SourceTypeEnum.C4) {
			USCISEmployee user = uscisEmployeeDAO.findByIcamSecUuid(securityContextService.getCurrentUserId());
			if (user != null && user.getEmployeeLocation() != null) {
				currentCaseLocation = user.getEmployeeLocation();
			}
		}

		logger.info("Assigning Current Location to {} for {}", currentCaseLocation.getLctnCd(), cisCase.getUscisReceiptNumber());
		cisCase.setAssignedCisCaseLocation(currentCaseLocation);
	}

	@Override
	public List<CaseActionHistoryView> getFilingCategoryHistory(long caseId) {
		return caseActionHandler.getHistoryByCaseAndCaseActionTypeCodes(caseId, Arrays.asList(Long.valueOf(CaseActionTypeCodeGenericEnum.ELIGIBILITY_REASON_UPDATED.getKeyId())));
	}
}
