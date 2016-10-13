package validation;

public class UserInputValidator {
	
	private static String inputRegex = "^[a-zA-Z0-9-_*#<>]{3,10}$";
	
	
	
	
	public static boolean validateUsername(String username){
		boolean valid = true;
		
		//Length validation
//		if(!UserInput.checkLength(
//				username,
//				UserInputConfig.USERNAME_MIN_LENGTH,
//				UserInputConfig.USERNAME_MAX_LENGTH)
//		) valid = false;
			
	 	return username.matches(UserInputValidator.inputRegex);
	}
	
	private static boolean checkLength(String str, int minLen, int maxLen){
		return str.length() >= minLen && str.length() <= maxLen;
	}
	
}
