package org.ace.insurance.web.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.system.common.keyfactor.KeyFactor;

public class SetUpIDChecker {

	/* KeyFactor */
	private static String SUM_INSURED = "SUM_INSURED";
	private static String SURRENDER_AGE = "SURRENDER_AGE";
	private static String POLICY_PERIOD = "POLICY_PERIOD";
	private static String PAYMENT_YEAR = "PAYMENT_YEAR";
	private static String LOADING = "LOADING";
	private static String CUBIC_CAPACITY = "CUBIC_CAPACITY";
	private static String BASED_ON_COVERAGE = "BASED_ON_COVERAGE";
	private static String BUILDINGOCCUPATION = "BUILDINGOCCUPATION";
	private static String CURRENCY = "CURRENCY";
	private static String PAYMENTTYPE = "PAYMENTTYPE";
	private static String GENDER = "GENDER";
	private static String AGE = "AGE";
	private static String RISKYOCCUPATION = "RISKYOCCUPATION";
	private static String SAFEOFPLACE = "SAFEOFPLACE";
	private static String IMPORTANT = "IMPORTANT";
	private static String SECURITY = "SECURITY";
	private static String SYSTEMATICALLY = "SYSTEMATICALLY";
	private static String NEARNCE = "NEARNCE";
	private static String OWNVEHUSE = "OWNVEHUSE";
	private static String BOXSAVE = "BOXSAVE";
	private static String MAINPLACE = "MAINPLACE";
	private static String PUBLICWAY = "PUBLICWAY";
	private static String BUILDINGCLASS = "BUILDINGCLASS";
	private static String ADDITIONAL_RISKY_ROOF = "ADDITIONAL_RISKY_ROOF";
	private static String TERM = "TERM";
	private static String POUND = "POUND";
	private static String DANGEROUS_OCCUPATION = "DANGEROUS_OCCUPATION";
	private static String CARGO_TYPE = "CARGO_TYPE";
	private static String ROUTE = "ROUTE";
	private static String MARINEHULL_AGE = "MARINEHULL_AGE";
	private static String MEDICAL_AGE = "MEDICAL_AGE";
	private static String COVERTYPE = "COVERTYPE";
	private static String TRAVEL_DAY = "TRAVEL_DAY";

	/* Product */
	private static String FIRE_BUILDING = "FIRE_BUILDING";
	private static String FIRE_MACHINERY = "FIRE_MACHINERY";
	private static String FIRE_FURNITURE = "FIRE_FURNITURE";
	private static String FIRE_STOCK = "FIRE_STOCK";
	private static String FIRE_DECLARATION_POLICY = "FIRE_DECLARATION_POLICY";
	private static String FIRE_ELECTRONIC = "FIRE_ELECTRONIC";
	private static String PRIVATE_CAR = "PRIVATE_CAR";
	private static String PRIVATE_TRUCK = "PRIVATE_TRUCK";
	private static String COMMERCIAL_CAR = "COMMERCIAL_CAR";
	private static String COMMERCIAL_TRUCK = "COMMERCIAL_TRUCK";
	private static String FA_CAR = "FA_CAR";
	private static String FA_TRUCK = "FA_TRUCK";
	private static String MOBILE_PLANT = "MOBILE_PLANT";
	private static String MOTOR_CYCLE = "MOTOR_CYCLE";
	private static String PRIVATE_CAR_USD = "PRIVATE_CAR_USD";
	private static String GOOD_CARRYING = "GOOD_CARRYING";
	private static String MOBILE_PLANT_USD = "MOBILE_PLANT_USD";
	private static String MARINE_CARGO = "MARINE_CARGO";
	private static String INLAND_CARGO = "INLAND_CARGO";
	private static String MARINE_COAST_CARGO = "MARINE_COAST_CARGO";
	private static String UNDER_100MILES_TRAVEL = "UNDER_100MILES_TRAVEL";
	private static String LOCAL_TRAVEL = "LOCAL_TRAVEL";
	private static String FOREIGN_TRAVEL = "FOREIGN_TRAVEL";
	private static String MARINEHULL_INLAND_TIME = "MARINEHULL_INLAND_TIME";
	private static String MARINEHULL_OVERSEAS_TIME = "MARINEHULL_OVERSEAS_TIME";
	private static String MARINEHULL_WOODEN_TIME = "MARINEHULL_WOODEN_TIME";
	private static String MARINEHULL_INLAND_VOYAGE = "MARINEHULL_INLAND_VOYAGE";
	private static String MARINEHULL_OVERSEAS_VOYAGE = "MARINEHULL_OVERSEAS_VOYAGE";
	private static String MARINEHULL_WOODEN_VOYAGE = "MARINEHULL_WOODEN_VOYAGE";
	private static String OVERSEAS_CARGO = "OVERSEAS_CARGO";
	private static String MOTORPRIVATE = "MOTORPRIVATE";
	private static String MOTORCOMMERCIAL = "MOTORCOMMERCIAL";
	private static String INDIVIDUAL_HEALTH_INSURANCE = "INDIVIDUAL_HEALTH_INSURANCE";
	private static String GROUP_HEALTH_INSURANCE = "GROUP_HEALTH_INSURANCE";
	private static String MICRO_HEALTH_INSURANCE = "MICRO_HEALTH_INSURANCE";
	private static String INDIVIDUAL_CRITICAL_ILLNESS_INSURANCE = "INDIVIDUAL_CRITICAL_ILLNESS_INSURANCE";
	private static String GROUP_CRITICAL_ILLNESS_INSURANCE = "GROUP_CRITICAL_ILLNESS_INSURANCE";

	private static String PERSONAL_ACCIDENT_KYT = "PERSONAL_ACCIDENT_KYT";
	private static String PERSONAL_ACCIDENT_USD = "PERSONAL_ACCIDENT_USD";

	/* Building Occupation */
	private static String BUILDING_OCC_NONE = "BUILDING_OCC_NONE";

	/* AddOn - ProductContent */
	private static String THIRD_PARTY = "THIRD_PARTY";
	private static String WINDSCREEN = "WINDSCREEN";
	private static String SUNROOF = "SUNROOF";
	private static String SRCC = "SRCC";
	private static String ACT_OF_GOD = "ACT_OF_GOD";
	private static String WARRISK = "WARRISK";
	private static String THEFT = "THEFT";
	private static String NIL_EXCESS = "NIL_EXCESS";
	private static String Air_Craft_Damage = "Air_Craft_Damage";
	private static String Earth_Quake = "Earth_Quake";
	private static String Flood_And_Inundation = "Flood_And_Inundation";
	private static String Riot_Strike_Malicious = "Riot_Strike_Malicious";
	private static String Subsidence_Landslide = "Subsidence_Landslide";
	private static String Burglary = "Burglary";
	private static String Explosion = "Explosion";
	private static String Impact_Damage = "Impact_Damage";
	private static String Spontaneous_Combustion = "Spontaneous_Combustion";
	private static String War_Risk = "War_Risk";
	private static String Storm_Tempest_Water = "Storm_Tempest_Water";
	private static String Storm_Tempest_Others = "Storm_Tempest_Others";
	private static String Storm_Tempest_Water_B = "Storm_Tempest_Water_B";
	private static String Storm_Tempest_Others_B = "Storm_Tempest_Others_B";
	private static String Port_Due_Date = "Port_Due_Date";

	/* Payment Type */
	private static String LUMPSUM = "LUMPSUM";

	/* Currency */
	private static String KYT_CUR = "KYT_CUR";
	private static String USD_CUR = "USD_CUR";

	/* */
	private static String RIVER = "RIVER";
	private static String CREEK = "CREEK";
	private static String SUMMER_COAST = "SUMMER_COAST";
	private static String RAINY_COAST = "RAINY_COAST";

	private static Properties idConfig;

	static {
		try {
			idConfig = new Properties();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream in = classLoader.getResourceAsStream("/setup-id-config.properties");
			idConfig.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load setup-id-config.properties");
		}
	}

	/* KeyFactor */
	public static boolean isSumInsured(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(SUM_INSURED).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isLoading(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(LOADING).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isCubicCapacity(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(CUBIC_CAPACITY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isBasedOnCoverage(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(BASED_ON_COVERAGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isBuildingClass(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(BUILDINGCLASS).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isBuildingOccupation(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(BUILDINGOCCUPATION).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isCurrency(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(CURRENCY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isAdditionalRiskyRoof(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(ADDITIONAL_RISKY_ROOF).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSafeOfPlace(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(SAFEOFPLACE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isImportant(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(IMPORTANT).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSecurity(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(SECURITY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isOwnvehUse(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(OWNVEHUSE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isBoxSave(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(BOXSAVE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPublicWay(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(PUBLICWAY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMainPlace(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(MAINPLACE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSystematically(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(SYSTEMATICALLY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isNearnce(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(NEARNCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isTerm(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(TERM).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isAge(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(AGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPound(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(POUND).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isDangerousOccupation(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(DANGEROUS_OCCUPATION).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isCargoType(KeyFactor keyFactor) {
		if (keyFactor.getId().trim().equals(idConfig.getProperty(CARGO_TYPE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isRoute(KeyFactor keyFactor) {
		if (keyFactor.getId().trim().equals(idConfig.getProperty(ROUTE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSurrenderAge(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(SURRENDER_AGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPaymentYear(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(PAYMENT_YEAR).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPolicyPeriod(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(POLICY_PERIOD).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPaymentType(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(PAYMENTTYPE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isGender(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(GENDER).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMedicalAge(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(MEDICAL_AGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHealth(String productId) {
		return isIndividualHealth(productId) || isGroupHealth(productId);
	}

	public static boolean isIndividualHealth(String productId) {
		if (productId.trim().equals(idConfig.getProperty(INDIVIDUAL_HEALTH_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isGroupHealth(String productId) {
		if (productId.trim().equals(idConfig.getProperty(GROUP_HEALTH_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMicroHealth(String productId) {
		if (productId.trim().equals(idConfig.getProperty(MICRO_HEALTH_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isCIProduct(String productId) {
		return isCriticalIllness(productId) || isGroupCriticalIllness(productId);
	}

	public static boolean isCriticalIllness(String productId) {
		if (productId.trim().equals(idConfig.getProperty(INDIVIDUAL_CRITICAL_ILLNESS_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isGroupCriticalIllness(String productId) {
		if (productId.trim().equals(idConfig.getProperty(GROUP_CRITICAL_ILLNESS_INSURANCE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMarineHullAge(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(MARINEHULL_AGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isRiskyOccupation(KeyFactor kf) {
		if (kf.getId().trim().equals(idConfig.getProperty(RISKYOCCUPATION).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isCoverType(KeyFactor keyFactor) {
		if (keyFactor.getId().trim().equals(idConfig.getProperty(COVERTYPE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isTravelDay(KeyFactor keyFactor) {
		if (keyFactor.getId().trim().equals(idConfig.getProperty(TRAVEL_DAY).trim())) {
			return true;
		}
		return false;
	}

	/* BuildigOccupation */
	public static boolean isBldNoneOccupation(String bldNoneOccpId) {
		if (bldNoneOccpId.equals(idConfig.getProperty(BUILDING_OCC_NONE).trim())) {
			return true;
		}
		return false;
	}

	/* Product */
	public static boolean isFireBuilding(String productId) {
		if (productId.trim().equals(idConfig.getProperty(FIRE_BUILDING).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFireMachinery(String productId) {
		if (productId.trim().equals(idConfig.getProperty(FIRE_MACHINERY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFireFurniture(String productId) {
		if (productId.trim().equals(idConfig.getProperty(FIRE_FURNITURE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFireStock(String productId) {
		if (productId.trim().equals(idConfig.getProperty(FIRE_STOCK).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFireDeclaration(String productId) {
		if (productId.trim().equals(idConfig.getProperty(FIRE_DECLARATION_POLICY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFireElectronic(String productId) {
		if (productId.trim().equals(idConfig.getProperty(FIRE_ELECTRONIC).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPrivateCar(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(PRIVATE_CAR).trim()) || product.getId().trim().equals(idConfig.getProperty(PRIVATE_CAR_USD).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPrivateTruck(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(PRIVATE_TRUCK).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isCommercialCar(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(COMMERCIAL_CAR).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isCommercialTruck(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(COMMERCIAL_TRUCK).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMotorPrivate(ProductGroup productgroup) {
		if (productgroup.getId().trim().equals(idConfig.getProperty(MOTORPRIVATE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMotorCommercial(ProductGroup productgroup) {
		if (productgroup.getId().trim().equals(idConfig.getProperty(MOTORCOMMERCIAL).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFaCar(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(FA_CAR).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFaTruck(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(FA_TRUCK).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMobilePlant(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(MOBILE_PLANT).trim()) || product.getId().trim().equals(idConfig.getProperty(MOBILE_PLANT_USD).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMotorCycle(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(MOTOR_CYCLE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isGoodCarrying(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(GOOD_CARRYING).trim())) {
			return true;
		}
		return false;
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

	public static boolean isUnder100MilesTravel(String productId) {
		if (productId.trim().equals(idConfig.getProperty(UNDER_100MILES_TRAVEL).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isLocalTravel(String productId) {
		if (productId.trim().equals(idConfig.getProperty(LOCAL_TRAVEL).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isOverseaTravel(String productId) {
		if (productId.trim().equals(idConfig.getProperty(FOREIGN_TRAVEL).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullInlandTime(String productId) {
		if (productId.trim().equals(idConfig.getProperty(MARINEHULL_INLAND_TIME).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullInlandVoyage(String productId) {
		if (productId.trim().equals(idConfig.getProperty(MARINEHULL_INLAND_VOYAGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullOverseasTime(String productId) {
		if (productId.trim().equals(idConfig.getProperty(MARINEHULL_OVERSEAS_TIME).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullOverseasVoyage(String productId) {
		if (productId.trim().equals(idConfig.getProperty(MARINEHULL_OVERSEAS_VOYAGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullWoodenTime(String productId) {
		if (productId.trim().equals(idConfig.getProperty(MARINEHULL_WOODEN_TIME).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isHullWoodenVoyage(String productId) {
		if (productId.trim().equals(idConfig.getProperty(MARINEHULL_WOODEN_VOYAGE).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isRenderSeating(String productId) {
		boolean result = false;
		productId = productId.trim();
		if (productId.equals(idConfig.getProperty(PRIVATE_CAR).trim()) || productId.equals(idConfig.getProperty(PRIVATE_CAR_USD).trim())
				|| productId.equals(idConfig.getProperty(COMMERCIAL_CAR).trim()) || productId.equals(idConfig.getProperty(FA_CAR).trim())) {
			result = true;
		}

		return result;
	}

	public static boolean isRenderLoading(String productId) {
		boolean result = false;
		productId = productId.trim();
		if (productId.equals(idConfig.getProperty(COMMERCIAL_TRUCK).trim()) || productId.equals(idConfig.getProperty(FA_TRUCK).trim())
				|| productId.equals(idConfig.getProperty(GOOD_CARRYING).trim()) || productId.equals(idConfig.getProperty(PRIVATE_TRUCK).trim())
				|| productId.equals(idConfig.getProperty(MOBILE_PLANT).trim()) || productId.equals(idConfig.getProperty(MOBILE_PLANT_USD).trim())) {
			result = true;
		}

		return result;
	}

	/* AddOn - ProductContent */
	public static boolean isThird_Party(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(THIRD_PARTY).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isWindScreen(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(WINDSCREEN).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSunRoof(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(SUNROOF).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSRCC(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(SRCC).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isActOfGod(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(ACT_OF_GOD).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isWarRisk(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(WARRISK).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isTheft(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(THEFT).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isNillExcess(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(NIL_EXCESS).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isAirCraftDamage(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Air_Craft_Damage).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isEarthQuake(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Earth_Quake).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isFloodAndInundation(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Flood_And_Inundation).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isRiotStrikeMalicious(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Riot_Strike_Malicious).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSubsidence_Landslide(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Subsidence_Landslide).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isBurglary(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Burglary).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isExplosion(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Explosion).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isImpactDamage(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Impact_Damage).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSpontaneousCombustion(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Spontaneous_Combustion).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isStormTempestOthers(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Storm_Tempest_Others).trim())
				|| productContentId.trim().equals(idConfig.getProperty(Storm_Tempest_Others_B).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isStormTempestWater(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Storm_Tempest_Water).trim())
				|| productContentId.trim().equals(idConfig.getProperty(Storm_Tempest_Water_B).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isWar_Risk(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(War_Risk).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPort_Due_Date(String productContentId) {
		if (productContentId.trim().equals(idConfig.getProperty(Port_Due_Date).trim())) {
			return true;
		}
		return false;
	}

	/* Payment Type */
	public static boolean isLumpsum(String paymentTypeId) {
		if (paymentTypeId.trim().equals(idConfig.getProperty(LUMPSUM).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isRiver(String id) {
		if (id.trim().equals(idConfig.getProperty(RIVER).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isCreek(String id) {
		if (id.trim().equals(idConfig.getProperty(CREEK).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isSummerCoast(String id) {
		if (id.trim().equals(idConfig.getProperty(SUMMER_COAST).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isRainyCoast(String id) {
		if (id.trim().equals(idConfig.getProperty(RAINY_COAST).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isOverseasCargo(String id) {
		if (id.trim().equals(idConfig.getProperty("OVERSEAS_CARGO").trim())) {
			return true;
		}
		return false;
	}

	public static boolean isMarineHullProduct(String id) {
		if (isHullInlandTime(id) || isHullInlandVoyage(id) || isHullOverseasTime(id) || isHullOverseasVoyage(id) || isHullWoodenTime(id) || isHullWoodenVoyage(id)) {
			return true;
		}
		return false;
	}

	public static boolean isKYTCur(String currencyId) {
		if (currencyId.trim().equals(idConfig.getProperty(KYT_CUR).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isUSDCur(String currencyId) {
		if (currencyId.trim().equals(idConfig.getProperty(USD_CUR).trim())) {
			return true;
		}
		return false;
	}

	public static String getUSDId() {
		return idConfig.getProperty(USD_CUR).trim();
	}

	public static String getMyanmarKyatCurrencyId() {
		return idConfig.getProperty(KYT_CUR).trim();
	}

	public static String getSumInsuredId() {
		return idConfig.getProperty(SUM_INSURED).trim();
	}

	public static String getCubicCapacityId() {
		return idConfig.getProperty(CUBIC_CAPACITY).trim();
	}

	public static String getLoadingId() {
		return idConfig.getProperty(LOADING).trim();
	}

	public static String getThirdPartyId() {
		return idConfig.getProperty(THIRD_PARTY).trim();
	}

	public static String getWinscreenId() {
		return idConfig.getProperty(WINDSCREEN).trim();
	}

	public static String getBasedOnCoverageId() {
		return idConfig.getProperty(BASED_ON_COVERAGE.trim());
	}

	public static String getBuildingClassId() {
		return idConfig.getProperty(BUILDINGCLASS.trim());
	}

	public static String getBuildingOccupationId() {
		return idConfig.getProperty(BUILDINGOCCUPATION.trim());
	}

	public static String getRiskyOccupationId() {
		return idConfig.getProperty(RISKYOCCUPATION.trim());
	}

	public static String getCurrencyId() {
		return idConfig.getProperty(CURRENCY.trim());
	}

	public static String getAdditionalRiskyRoofId() {

		return idConfig.getProperty(ADDITIONAL_RISKY_ROOF.trim());
	}

	public static String getCargoTypeId() {
		return idConfig.getProperty(CARGO_TYPE).trim();
	}

	public static String getRouteId() {
		return idConfig.getProperty(ROUTE).trim();
	}

	public static String getCoverTypeId() {
		return idConfig.getProperty(COVERTYPE).trim();
	}

	public static String getMarineHullAgeId() {
		return idConfig.getProperty(MARINEHULL_AGE).trim();
	}
	public static String getPaymentTypeLumpsumId() {
		return idConfig.getProperty(LUMPSUM);
	}

	public static boolean isPersonalAccident(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(PERSONAL_ACCIDENT_KYT).trim())) {
			return true;
		} else if (product.getId().trim().equals(idConfig.getProperty(PERSONAL_ACCIDENT_USD).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPersonalAccidentUSD(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(PERSONAL_ACCIDENT_USD).trim())) {
			return true;
		}
		return false;
	}

	public static boolean isPersonalAccidentKYT(Product product) {
		if (product.getId().trim().equals(idConfig.getProperty(PERSONAL_ACCIDENT_KYT).trim())) {
			return true;
		}
		return false;
	}

	public static String getMicroHealthId() {
		return idConfig.getProperty(MICRO_HEALTH_INSURANCE);
	}

	public static String getIndividualHealththId() {
		return idConfig.getProperty(INDIVIDUAL_HEALTH_INSURANCE);
	}

	public static String getGroupHealththId() {
		return idConfig.getProperty(GROUP_HEALTH_INSURANCE);
	}

	public static String getIndividualCriticalIllnessId() {
		return idConfig.getProperty(INDIVIDUAL_CRITICAL_ILLNESS_INSURANCE);
	}

	public static String getGroupCriticalIllnessId() {
		return idConfig.getProperty(GROUP_CRITICAL_ILLNESS_INSURANCE);
	}
}
