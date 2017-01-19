package cz.kiv.ups.validation;

import cz.kiv.ups.config.AppConfig;

public class UserInputValidator {

	public static boolean validateUsername(String username){
		return username.matches(AppConfig.USERNAME_REGEX);
	}
	
	private static boolean checkLength(String str, int minLen, int maxLen){
		return str.length() >= minLen && str.length() <= maxLen;
	}

}
