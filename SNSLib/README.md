##Download:

Add library dependency to your build.gradle file:
```gradle

buildscript {
  repositories {
    maven { url 'https://maven.fabric.io/public' }
  }

  dependencies {
    // The Fabric Gradle plugin uses an open ended version to react
    // quickly to Android tooling updates
    classpath 'io.fabric.tools:gradle:1.+'
  }
}

apply plugin: 'io.fabric'

repositories {
  maven { url 'https://maven.fabric.io/public' }
}


dependencies {
   compile 'rikkei.android:SNSLib:1.0'
}
```

Adding the following code to AndroidManifest.xml file under </application> tag:
```gradle
  <meta-data
            android:name="io.fabric.ApiKey"
            android:value="YOUR_API_KEY"/>
```		
  (Sign up Account at https://www.fabric.io and  goto https://fabric.io/kits/android/twitterkit/install to get YOUR_API_KEY)


## Usage

Put statement: 
      RkTwitterUtils.init(Context context, String consumerKey , String consumerSecret)
	  to first line of onCreate() method of every Activity
	(Sign Up an account, login to https://apps.twitter.com/ and create an Application to get **consumerKey**, **consumerSecret** keys)
Put statement: 
      RkTwitterUtils.getInstance().onActivityResult(requestCode, resultCode, data);
      to onActivityResult()  of activity
	  
Call util functions: 
	  RkTwitterUtils.login(...), RkTwitterUtils.logout(...), RkTwitterUtils.post(...)...
	  
	  
## License

    Copyright 2016 by Rikkeisoft Co., Ltd

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

