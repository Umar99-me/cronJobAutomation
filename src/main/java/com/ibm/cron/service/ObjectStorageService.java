package com.ibm.cron.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import com.ibm.cloud.objectstorage.services.s3.model.AmazonS3Exception;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectRequest;
import com.ibm.cron.model.CronDetailsModel;

@Service
public class ObjectStorageService {
	 final String bucketName = "9cronsbucket9";  
	 final String COS_ENDPOINT = "https://s3.jp-tok.cloud-object-storage.appdomain.cloud"; 
	 final String COS_API_KEY_ID = "M1pq18Tgq95sc6XNAKJJ8DfqzyquuisBPb20jzWqCZZU"; 
	 final String COS_AUTH_ENDPOINT = "https://iam.cloud.ibm.com/identity/token";
	 final String COS_SERVICE_CRN = "crn:v1:bluemix:public:cloud-object-storage:global:a/73bc886187e0442fa30c6cdfda79472b:d94b1203-b77a-48cc-90ac-e627ef5ccfc1::"; 
	 final String COS_BUCKET_LOCATION = "jp-tok"; 
	 final String storageClass = "us-south-standard";
	//static String Cron_statement;
	
	public AmazonS3 createClient()
	{
		AWSCredentials credentials = new BasicIBMOAuthCredentials(COS_API_KEY_ID, COS_SERVICE_CRN);
		ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(5000);
		clientConfig.setUseTcpKeepAlive(true);

		AmazonS3 cos = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withEndpointConfiguration(new EndpointConfiguration(COS_ENDPOINT, COS_BUCKET_LOCATION)).withPathStyleAccessEnabled(true)
				.withClientConfiguration(clientConfig).build();

		return cos;
	}
	public List<CronDetailsModel> getObjects(AmazonS3 cos,String file_key) throws FileNotFoundException {
		//System.out.println(file_key);
		GetObjectRequest request = new GetObjectRequest(bucketName,file_key);
		try {
			cos.getObject(request, new File("C:/testing/model.txt"));
	        List<CronDetailsModel> crons=new ArrayList<>();
	        Set<String> set=new HashSet<>();
			Scanner read = new Scanner(new File("C:/testing/model.txt"));
			while(read.hasNextLine())
			{
				set.add(read.nextLine());
			}
			for(String eachLine:set)
			{
				CronDetailsModel eachCron=new CronDetailsModel();
				String[] details=eachLine.split(";");
				if(details.length>=2) {
					String[] result=buildNameAndParameter(details[0]);
					eachCron.setCronName(result[0]);
					eachCron.setParameters(result[1]);
					eachCron.setDescription(details[1]);
					crons.add(eachCron);
				}	
			}
			read.close();
			return crons;
		} catch (AmazonS3Exception ex) {
			return new ArrayList<CronDetailsModel>();
		}
		
	}
	
	public String[] buildNameAndParameter(String field) {
		String[] result=new String[2];
		String[] nameAndparam=field.split(" ");
		//String[] data=field.split("1>");
		//String [] nameAndparam=
		String parametrs="";
		String Parameter="";
		for(int i=0;i<nameAndparam.length;i++)
		{
			System.out.println(nameAndparam[i]);
			if(i==0) {
				result[0]=nameAndparam[i];
			}
			
			else if(nameAndparam[i].equals("1>"))
			{
				break;
			}
			else 
			{
				parametrs+=nameAndparam[i];
				parametrs+=",";
			}
			//System.out.println(nameAndparam[i]);
		}
		//parametrs.charAt(parametrs.length()-1)="";
		if(!parametrs.equals("")) {
		Parameter=parametrs.substring(0,parametrs.length()-1);}
		result[1]=Parameter;
		return result;
	}
	public void putFile(AmazonS3 cos,String file_path) {
		cos.putObject(bucketName, "spcron",new File(file_path));
	}

}
