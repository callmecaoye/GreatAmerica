package com.promeets.android.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import com.promeets.android.MyApplication;
import com.promeets.android.pojo.ServiceResponse;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

public class PromeetsPreferenceUtil {

	public static final int INTEGER_RETURN_TYPE = 1;
	public static final int FLOAT_RETURN_TYPE = 2;
	public static final int LONG_RETURN_TYPE = 3;
	public static final int BOOLEAN_RETURN_TYPE = 4;
	public static final int STRING_RETURN_TYPE = 5;


	private final SharedPreferences mSharedPreference;
	private final String MT_SHARED_PREFERENCE_NAME = "MT_SHARED_PREFERENCE_NAME";

	public static final String USER_OBJECT_KEY = "UserObject";
	public static final String USER_SESSION_ID = "SessionId";
	public static final String USER_PROFILE_OBJECT_KEY = "UserProfileObject";
	public static final String EXPERT_PROFILE_OBJECT_KEY = "ExpertProfileObject";

	public PromeetsPreferenceUtil() {
		mSharedPreference = MyApplication.getContext().getSharedPreferences(
				MT_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public <T> void setValue(String key, T value)  {

		if (!(value instanceof Integer || value instanceof Float
				|| value instanceof Boolean || value instanceof String || value instanceof Long)) {
			Log.e("SAVE ERROR","Object of type " + value.getClass().getName()+" not allowed in shared preference");
			return;
		}

		
		Editor editor = mSharedPreference.edit();
		
		editor.putString(key, value.toString());
		
		editor.commit();
	}
	
	/**
	 * @param key
	 * @param returnType
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public  <T> T getValue(String key,int returnType)
	{
		T retriveObject = null;
		

		String decryptedText = /*cryptoUtil.decrypt(*/mSharedPreference.getString(key, null)/*)*/;
		
		
		if(decryptedText==null){
			return null;
		}
		
		NumberFormatUtil numberFormatUtil = NumberFormatUtil.getInstance();
		
		switch(returnType){
		case INTEGER_RETURN_TYPE:
			retriveObject = (T) new Integer(numberFormatUtil.getInt(decryptedText)); break;
		
		case FLOAT_RETURN_TYPE:
			retriveObject = (T) new Float(numberFormatUtil.getFloat(decryptedText)); break;
			
		case STRING_RETURN_TYPE:
			retriveObject = (T) decryptedText;break;
			
		case BOOLEAN_RETURN_TYPE:
			retriveObject = (T) new Boolean(decryptedText); break;
			
		case LONG_RETURN_TYPE:
			retriveObject = (T) new Long(numberFormatUtil.getLong(decryptedText)); break;
		}
		
		return retriveObject;
	}

	@SuppressWarnings("unchecked")
	public  <T> T getValue(String key,Class classOf)
	{
		String storedValue = getValue(key,STRING_RETURN_TYPE);
		if(!StringUtils.isEmpty(storedValue)){
			ServiceResponse serviceResponse = new ServiceResponse();
			serviceResponse.setServiceResponse(storedValue);

			return (T) serviceResponse.getServiceResponse(classOf);
		}else
			return null;
	}

	public void clear(){

		Editor editor = mSharedPreference.edit();
		editor.clear();
		editor.commit();
	}
}