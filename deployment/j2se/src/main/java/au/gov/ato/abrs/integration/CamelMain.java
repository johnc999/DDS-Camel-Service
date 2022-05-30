package au.gov.ato.abrs.integration;

import au.gov.ato.abrs.integration.main.CamelServiceMain;

//@Author: Johnathan Ingram (johnathan.ingram@ato.gov.au)
public class CamelMain {
	public static void main(String[] args) throws Exception {
		CamelServiceMain.runCamelService(args, CamelMain.class, new Module());	
	}
}