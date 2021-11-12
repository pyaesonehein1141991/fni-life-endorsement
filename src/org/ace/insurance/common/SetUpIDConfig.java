package org.ace.insurance.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.java.component.SystemException;

public class SetUpIDConfig {
	/* KeyFactor */
	private static String SUM_INSURED = "SUM_INSURED";
	private static String BUILDINGOCCUPATION = "BUILDINGOCCUPATION";
	private static String CURRENCY = "CURRENCY";
	private static String AGE = "AGE";
	private static String BUILDINGCLASS = "BUILDINGCLASS";
	private static String ADDITIONAL_RISKY_ROOF = "ADDITIONAL_RISKY_ROOF";
	private static String CARGO_TYPE = "CARGO_TYPE";
	private static String ROUTE = "ROUTE";

	/* Product */
	private static String FIRE_BUILDING = "FIRE_BUILDING";
	private static String FIRE_MACHINERY = "FIRE_MACHINERY";
	private static String FIRE_FURNITURE = "FIRE_FURNITURE";
	private static String FIRE_STOCK = "FIRE_STOCK";
	private static String FIRE_DECLARATION_POLICY = "FIRE_DECLARATION_POLICY";
	private static String MOTOR_CYCLE = "MOTOR_CYCLE";
	private static String MARINE_CARGO = "MARINE_CARGO";
	private static String INLAND_CARGO = "INLAND_CARGO";
	private static String MARINE_COAST_CARGO = "MARINE_COAST_CARGO";
	private static String UNDER_100MILES_TRAVEL = "UNDER_100MILES_TRAVEL";
	private static String LOCAL_TRAVEL = "LOCAL_TRAVEL";
	private static String FOREIGN_TRAVEL = "FOREIGN_TRAVEL";

	private static String MARINEHULL_INLANDTIME = "MARINEHULL_INLANDTIME";
	private static String MARINEHULL_INLANDVOYAGE = "MARINEHULL_INLANDVOYAGE";
	private static String MARINEHULL_OVERSEATIME = "MARINEHULL_OVERSEATIME";
	private static String MARINEHULL_OVERSEAVOYAGE = "MARINEHULL_OVERSEAVOYAGE";
	private static String MARINEHULL_INLANDWOODEN = "MARINEHULL_INLANDWOODEN";

	private static String INDIVIDUAL_HEALTH_INSURANCE = "INDIVIDUAL_HEALTH_INSURANCE";
	private static String GROUP_HEALTH_INSURANCE = "GROUP_HEALTH_INSURANCE";
	private static String MICRO_HEALTH_INSURANCE = "MICRO_HEALTH_INSURANCE";
	private static String INDIVIDUAL_CRITICAL_ILLNESS_INSURANCE = "INDIVIDUAL_CRITICAL_ILLNESS_INSURANCE";
	private static String GROUP_CRITICAL_ILLNESS_INSURANCE = "GROUP_CRITICAL_ILLNESS_INSURANCE";

	private static String PERSONAL_ACCIDENT_KYT = "PERSONAL_ACCIDENT_KYT";
	private static String PERSONAL_ACCIDENT_USD = "PERSONAL_ACCIDENT_USD";

	/* AddOn - ProductContent */
	private static String THIRD_PARTY = "THIRD_PARTY";
	private static String NIL_EXCESS = "NIL_EXCESS";

	/* Product Group */
	private static String FIRE = "FIRE";

	/* Branch */
	private static String YANGON_BRANCH = "YANGON_BRANCH";

	/* CoInsurance Company */
	private static String MI = "MI";

	/* Relationship */
	private static String SELF_RELATIONSHIP = "SELF_RELATIONSHIP";

	/* Currency */
	private static String USD_CUR = "USD_CUR";
	private static String KYT_CUR = "KYT_CUR";

	/* Transaction Type */
	private static String CSCREDIT = "CSCREDIT";
	private static String CSDEBIT = "CSDEBIT";
	private static String TRDEBIT = "TRDEBIT";
	private static String TRCREDIT = "TRCREDIT";

	/* Policy Code List */
	private static String FIREPOLICYCODE = "FIREINSUREANCEPOLICY";
	private static String FIRERENEWALINSURANCEPOLICYCODE = "FIRERENEWALINSURANCEPOLICY";
	private static String CASHINSAFEINSURANCEPOLICYCODE = "CASHINSAFEINSURANCEPOLICY";
	private static String CASHINSAFERENEWALINSURACEPOLICYCODE = "CASHINSAFERENEWALINSURACEPOLICY";
	private static String CASHINTRANSITINSURANCEPOLICYCODE = "CASHINTRANSITINSURANCEPOLICY";
	private static String FIDELITYINSURANCEPOLICYCODE = "FIDELITYINSURANCEPOLICY";
	private static String MOTORPRIVATEINSURANCEPOLICYCODE = "MOTORPRIVATEINSURANCEPOLICY";
	private static String MOTORCOMMERCIALINSURANCEPOLICYCODE = "MOTORCOMMERCIALINSURANCEPOLICY";
	private static String MOTORPRIVATERENEALINSURANCEPOLICYCODE = "MOTORPRIVATERENEALINSURANCEPOLICY";
	private static String MOTORCOMMERCIALRENEWALINSURANCEPOLICYCODE = "MOTORCOMMERCIALRENEWALINSURANCEPOLICY";
	private static String MOTORFLEETINSURANCEPOLICYCODE = "MOTORFLEETINSURANCEPOLICY";
	private static String MOTORMOBILEPLANTINSURANCEPOLICYCODE = "MOTORMOBILEPLANTINSURANCEPOLICY";
	private static String MOTORSOCIALINSURANCEPOLICYCODE = "MOTORSOCIALINSURANCEPOLICY";
	private static String MOTORMOTORCYCLEINSURANCEPOLICYCODE = "MOTORMOTORCYCLEINSURANCEPOLICY";
	private static String MOTORFLEETRENEWALINSURANCEPOLICYCODE = "MOTORFLEETRENEWALINSURANCEPOLICY";
	private static String MOTORMOBILEPLANTRENEWALINSURANCEPOLICYCODE = "MOTORMOBILEPLANTRENEWALINSURANCEPOLICY";
	private static String MOTORSOCIALRENEWALINSURANCEPOLICYCODE = "MOTORSOCIALRENEWALINSURANCEPOLICY";
	private static String MOTORMOTORCYCLERENEWALINSURANCEPOLICYCODE = "MOTORMOTORCYCLERENEWALINSURANCEPOLICY";
	private static String INLANDTIMEWOODENMARINEHULLINSURANCEPOLICYCODE = "INLANDTIMEWOODENMARINEHULLINSURANCEPOLICY";
	private static String INLANDVOYAGEWOODENMARINEHULLINSURANCEPOLICYCODE = "INLANDVOYAGEWOODENMARINEHULLINSURANCEPOLICY";
	private static String CARGOMARINEINSURANCEPOLICYCODE = "CARGOMARINEINSURANCEPOLICY";
	private static String CARGOMARINECOASTINSURANCEPOLICYCODE = "CARGOMARINECOASTINSURANCEPOLICY";
	private static String CAROGINLANDINSURANCEPOLICYCODE = "CAROGINLANDINSURANCEPOLICY";
	private static String INLANDTIMEMARINEHULLINSURANCEPOLICYCODE = "INLANDTIMEMARINEHULLINSURANCEPOLICY";
	private static String INLANDVOYAGEMARINEHULLINSURANCEPOLICYCODE = "INLANDVOYAGEMARINEHULLINSURANCEPOLICY";
	private static String OVERSEATIMEMARINEHULLINSURANCEPOLICYCODE = "OVERSEATIMEMARINEHULLINSURANCEPOLICY";
	private static String OVERSEAVOYAGEMARINEHULLINSURANCEPOLICYCODE = "OVERSEAVOYAGEMARINEHULLINSURANCEPOLICY";
	private static String OVERSEACARGOINSURANCEPOLICYCODE = "OVERSEACARGOINSURANCEPOLICY";
	private static String PERSONALACCIDENTINSURANCEPOLICYCODE = "PERSONALACCIDENTINSURANCEPOLICY";
	private static String HEALTHINSURANCEPOLICYCODE = "HEALTHINSURANCEPOLICY";
	private static String CRITICALILLNESSINSURANCEPOLICYCODE = "CRITICALILLNESSINSURANCEPOLICY";
	private static String MICROHEALTHINSURANCEPOLICYCODE = "MICROHEALTHINSURANCEPOLICY";
	/* Proposal ISO-NO and Date */
	private static String MOTORPROPOSALISOCODE = "MOTORPROPOSAL_ISOCODE";
	private static String FIDELITYINSURANCEPROPOSALCODE = "FIDELITYINSURANCEPROPOSAL";
	private static String CASHINSAFEINSURANCEPROPOSALCODE = "FIDELITYINSURANCEPROPOSAL";
	private static String CASHINTRANSITINSURANCEPROPOSALCODE = "FIDELITYINSURANCEPROPOSAL";
	private static String FIREINSURANCEPROPOSALCODE = "FIREINSURANCEPROPOSAL";
	private static String CARGOINLANDINSURANCEPROPOSALCODE = "CARGOINLANDINSURANCEPROPOSAL";
	private static String CARGOMARINEINSURANCEPROPOSALCODE = "CARGOMARINEINSURANCEPROPOSAL";
	private static String MARINEHULLINSURANCEPROPOSALCODE = "MARINEHULLINSURANCEPROPOSAL";
	private static String OVERSEACARGOINSURANCEPROPOSALCODE = "OVERSEACARGOINSURANCEPROPOSAL";

	/* product content */
	private static String OPERATION_MED_ADDON_1 = "OPERATION_MED_ADDON_1";
	private static String CLINICAL_MED_ADDON_2 = "CLINICAL_MED_ADDON_2";

	private static Properties idConfig;

	static {
		try {
			idConfig = new Properties();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream in = classLoader.getResourceAsStream("setup-id-config.properties");
			idConfig.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load setup-id-config.properties");
		}
	}

	/* Currency */
	public static String getUSDCurrencyId() {
		return idConfig.getProperty(USD_CUR);
	}

	public static String getKYTCurrencyId() {
		return idConfig.getProperty(KYT_CUR);
	}

	public static String getNilExcessId() {
		return idConfig.getProperty(NIL_EXCESS);
	}

	public static String getBuildingClassId() {
		return idConfig.getProperty(BUILDINGCLASS);
	}

	public static String getCurrencyId() {
		return idConfig.getProperty(CURRENCY);
	}

	public static String getBuildingOccupationId() {
		return idConfig.getProperty(BUILDINGOCCUPATION);
	}

	public static String getAdditionalRiskyRoofId() {
		return idConfig.getProperty(ADDITIONAL_RISKY_ROOF);
	}

	public static String getFireBuildingId() {
		return idConfig.getProperty(FIRE_BUILDING);
	}

	public static String getFireMachineryId() {
		return idConfig.getProperty(FIRE_MACHINERY);
	}

	public static String getFireFurnitureId() {
		return idConfig.getProperty(FIRE_FURNITURE);
	}

	public static String getFireStockId() {
		return idConfig.getProperty(FIRE_STOCK);
	}

	public static String getFireDelclarationPolicyId() {
		return idConfig.getProperty(FIRE_DECLARATION_POLICY);
	}

	public static String getMotorCycleId() {
		return idConfig.getProperty(MOTOR_CYCLE);
	}

	public static String getThirdPartyId() {
		return idConfig.getProperty(THIRD_PARTY);
	}

	public static String getYangonBranchId() {
		return idConfig.getProperty(YANGON_BRANCH);
	}

	public static String getOperationAndMis() {
		return idConfig.getProperty(OPERATION_MED_ADDON_1);
	}

	public static String getClinical() {
		return idConfig.getProperty(CLINICAL_MED_ADDON_2);
	}

	public static boolean isMYANMA_INSURANCE(String coCompanyId) {
		if (coCompanyId.trim().equals(idConfig.getProperty(MI))) {
			return true;
		}
		return false;
	}

	public static boolean isSumInsured(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(SUM_INSURED))) {
			return true;
		}
		return false;
	}

	public static boolean isFireDeclarationPolicy(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(FIRE_DECLARATION_POLICY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFireProductGroup(ProductGroup productGroup) {
		if (productGroup.getId().trim().equals(idConfig.getProperty(FIRE).trim())) {
			return true;
		}
		return false;
	}

	public static String getFireProductGroupID() {
		return idConfig.getProperty(FIRE);
	}

	public static String getCargoTypeId() {
		return idConfig.getProperty(CARGO_TYPE).trim();
	}

	public static String getRouteId() {
		return idConfig.getProperty(ROUTE).trim();
	}

	public static String getAgeId() {
		return idConfig.getProperty(AGE).trim();
	}

	public static String getSumInsuredId() {
		return idConfig.getProperty(SUM_INSURED);
	}

	public static String getMarineCoastalInsuranceId() {
		return idConfig.getProperty(MARINE_CARGO).trim();
	}

	public static String getInLandRoadAndRailInsuranceId() {
		return idConfig.getProperty(INLAND_CARGO).trim();
	}

	public static boolean isMarineCargo(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(MARINE_CARGO).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMarineCoastCargo(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(MARINE_COAST_CARGO).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isInlandCargo(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(INLAND_CARGO).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isLocalTravelInsurance(Product product) {
		if (product.getId().trim().endsWith(idConfig.getProperty(LOCAL_TRAVEL).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isUnder100MileTravelInsurance(Product product) {
		if (product.getId().trim().endsWith(idConfig.getProperty(UNDER_100MILES_TRAVEL).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isForeignTravelInsurance(Product product) {
		if (product.getId().trim().endsWith(idConfig.getProperty(FOREIGN_TRAVEL).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullInlandTime(Product product) {
		if (product.getId().trim().endsWith(idConfig.getProperty(MARINEHULL_INLANDTIME).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullInlandVoyage(Product product) {
		if (product.getId().trim().endsWith(idConfig.getProperty(MARINEHULL_INLANDVOYAGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullOverseaTime(Product product) {
		if (product.getId().trim().endsWith(idConfig.getProperty(MARINEHULL_OVERSEATIME).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullOverseaVoyage(Product product) {
		if (product.getId().trim().endsWith(idConfig.getProperty(MARINEHULL_OVERSEAVOYAGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullInlandWooden(Product product) {
		if (product.getId().trim().endsWith(idConfig.getProperty(MARINEHULL_INLANDWOODEN).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSelfRelationship(String relationshipId) {
		if (relationshipId.trim().equals(idConfig.getProperty(SELF_RELATIONSHIP).trim())) {
			return true;
		}
		return false;
	}

	public static String getGroupHealthInsuranceId() {
		return idConfig.getProperty(GROUP_HEALTH_INSURANCE).trim();
	}

	public static String getIndividualCriticalIllness_Id() {
		return idConfig.getProperty(INDIVIDUAL_CRITICAL_ILLNESS_INSURANCE).trim();
	}

	public static String getGroupCriticalIllness_Id() {
		return idConfig.getProperty(GROUP_CRITICAL_ILLNESS_INSURANCE).trim();
	}

	public static String getMicroHealthInsurance() {
		return idConfig.getProperty(MICRO_HEALTH_INSURANCE).trim();
	}

	public static String getIndividualHealthInsuranceId() {
		return idConfig.getProperty(INDIVIDUAL_HEALTH_INSURANCE).trim();
	}

	// NEW MEDICAL
	public static boolean isIndividualHealthInsurance(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(INDIVIDUAL_HEALTH_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isGroupHealthInsurancae(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(GROUP_HEALTH_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMicroHealthInsurance(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(MICRO_HEALTH_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isGroupCriticalIllnessInsurance(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(GROUP_CRITICAL_ILLNESS_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isIndividualCriticalIllnessInsurance(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(INDIVIDUAL_CRITICAL_ILLNESS_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static String getCSCredit() {
		return idConfig.getProperty(CSCREDIT);
	}

	public static String getCSDebit() {
		return idConfig.getProperty(CSDEBIT);
	}

	public static String getTRCredit() {
		return idConfig.getProperty(TRCREDIT);
	}

	public static String getTRDebit() {
		return idConfig.getProperty(TRDEBIT);
	}

	public static String getFirePolicyCode() {
		return idConfig.getProperty(FIREPOLICYCODE);
	}

	public static String getFireRenewPolicyCode() {
		return idConfig.getProperty(FIRERENEWALINSURANCEPOLICYCODE);
	}

	public static String getCashInSafePolicyCode() {
		return idConfig.getProperty(CASHINSAFEINSURANCEPOLICYCODE);
	}

	public static String getCashInSafeRenewPolicyCode() {
		return idConfig.getProperty(CASHINSAFERENEWALINSURACEPOLICYCODE);
	}

	public static String getCashInTansitPolicyCode() {
		return idConfig.getProperty(CASHINTRANSITINSURANCEPOLICYCODE);
	}

	public static String getFidelityPolicyCode() {
		return idConfig.getProperty(FIDELITYINSURANCEPOLICYCODE);
	}

	public static String getMotorPrivatePolicyCode() {
		return idConfig.getProperty(MOTORPRIVATEINSURANCEPOLICYCODE);
	}

	public static String getMotorCommercialPolicyCode() {
		return idConfig.getProperty(MOTORCOMMERCIALINSURANCEPOLICYCODE);
	}

	public static String getMotorPrivateRenewPolicyCode() {
		return idConfig.getProperty(MOTORPRIVATERENEALINSURANCEPOLICYCODE);
	}

	public static String getMotorCommercialRenewPolicyCode() {
		return idConfig.getProperty(MOTORCOMMERCIALRENEWALINSURANCEPOLICYCODE);
	}

	public static String getCargoMarinePolicyCode() {
		return idConfig.getProperty(CARGOMARINEINSURANCEPOLICYCODE);
	}

	public static String getCargoMarineCoastPolicyCode() {
		return idConfig.getProperty(CARGOMARINECOASTINSURANCEPOLICYCODE);
	}

	public static String getCargoInlandPolicyCode() {
		return idConfig.getProperty(CAROGINLANDINSURANCEPOLICYCODE);
	}

	public static String getInlandTimeMarineHullPolicyCode() {
		return idConfig.getProperty(INLANDTIMEMARINEHULLINSURANCEPOLICYCODE);
	}

	public static String getInlandVoyageMarineHullPolicyCode() {
		return idConfig.getProperty(INLANDVOYAGEMARINEHULLINSURANCEPOLICYCODE);
	}

	public static String getOverseaTimeMarineHullPolicyCode() {
		return idConfig.getProperty(OVERSEATIMEMARINEHULLINSURANCEPOLICYCODE);
	}

	public static String getOverseaVoyageMarineHullPolicyCode() {
		return idConfig.getProperty(OVERSEAVOYAGEMARINEHULLINSURANCEPOLICYCODE);
	}

	public static String getOverseaCargoPolicyCode() {
		return idConfig.getProperty(OVERSEACARGOINSURANCEPOLICYCODE);
	}

	public static String getMotorFleetPolicycode() {
		return idConfig.getProperty(MOTORFLEETINSURANCEPOLICYCODE);
	}

	public static String getMotorMobilePlantPolicyCode() {
		return idConfig.getProperty(MOTORMOBILEPLANTINSURANCEPOLICYCODE);
	}

	public static String getMotorSocialPolicyCode() {
		return idConfig.getProperty(MOTORSOCIALINSURANCEPOLICYCODE);
	}

	public static String getMotorMotorCyclePolicyCode() {
		return idConfig.getProperty(MOTORMOTORCYCLEINSURANCEPOLICYCODE);
	}

	public static String getMotorFleetRenewalPolicyCode() {
		return idConfig.getProperty(MOTORFLEETRENEWALINSURANCEPOLICYCODE);
	}

	public static String getMotorMobilePlantRenewalPolicyCode() {
		return idConfig.getProperty(MOTORMOBILEPLANTRENEWALINSURANCEPOLICYCODE);
	}

	public static String getMotorSocialRenewalPolicyCode() {
		return idConfig.getProperty(MOTORSOCIALRENEWALINSURANCEPOLICYCODE);
	}

	public static String getMotorMotorCycleRenewalPolicyCode() {
		return idConfig.getProperty(MOTORMOTORCYCLERENEWALINSURANCEPOLICYCODE);
	}

	public static String getInlandWoodenMarineHullPolicyCode() {
		return idConfig.getProperty(INLANDTIMEWOODENMARINEHULLINSURANCEPOLICYCODE);
	}

	public static String getInlandVoyageWoodenMarineHullPolicyCode() {
		return idConfig.getProperty(INLANDVOYAGEWOODENMARINEHULLINSURANCEPOLICYCODE);
	}

	public static String getMotorProposalIsoCode() {
		return idConfig.getProperty(MOTORPROPOSALISOCODE);
	} 
	
	public static String getFIDELITYINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(FIDELITYINSURANCEPROPOSALCODE);
	}

	public static String getCASHINSAFEINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(CASHINSAFEINSURANCEPROPOSALCODE);
	}

	public static String getCASHINTRANSITINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(CASHINTRANSITINSURANCEPROPOSALCODE);
	}

	public static String getFIREINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(FIREINSURANCEPROPOSALCODE);
	}

	public static String getCARGOINLANDINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(CARGOINLANDINSURANCEPROPOSALCODE);
	}

	public static String getCARGOMARINEINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(CARGOMARINEINSURANCEPROPOSALCODE);
	}

	public static String getMARINEHULLINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(MARINEHULLINSURANCEPROPOSALCODE);
	}

	public static String getOVERSEACARGOINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(OVERSEACARGOINSURANCEPROPOSALCODE);
	}
	public static String getPERSONALACCIDENTINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(PERSONALACCIDENTINSURANCEPOLICYCODE);
	}

	public static String getHEALTHINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(HEALTHINSURANCEPOLICYCODE);
	}

	public static String getCRITICALILLNESSINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(CRITICALILLNESSINSURANCEPOLICYCODE);
	}

	public static String getMICROHEALTHINSURANCEPROPOSALCODE() {
		return idConfig.getProperty(MICROHEALTHINSURANCEPOLICYCODE);
	}

	public static String getPersonalAccidentMMKId() {
		return idConfig.getProperty(PERSONAL_ACCIDENT_KYT);
	}

	public static String getPersonalAccidentUSDId() {
		return idConfig.getProperty(PERSONAL_ACCIDENT_USD);
	}

}
