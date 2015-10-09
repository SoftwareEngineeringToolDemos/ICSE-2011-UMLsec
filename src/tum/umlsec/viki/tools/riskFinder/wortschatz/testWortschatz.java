package tum.umlsec.viki.tools.riskFinder.wortschatz;


public class testWortschatz {
	
	public static void main(String args[]) {
		try {
		SynonymsClient sc = new SynonymsClient();
		CooccurrencesClient cc = new CooccurrencesClient();
		String[][] result1 = null;
		String[][] result2 = null;
		String username = "anonymous";
		String password = "anonymous";
		String corpus = "de";
		String limit = "10";
		String signifikanz = "10";
		String searchPattern = "verschlüsseln";
		
		sc.setUsername(username);
		sc.setPassword(password);
		sc.setCorpus(corpus);
		sc.addParameter("Wort", searchPattern);
		sc.addParameter("Limit", limit);
		
		sc.execute();
		result1 = sc.getResult();
		System.out.println("länge: " + result1.length);
		for (int i = 0; i < result1.length; i++){
			System.out.println(result1[i][0]);
		}
		
		cc.setCorpus(corpus);
		cc.setPassword(password);
		cc.setUsername(username);
		cc.addParameter("Wort", searchPattern);
		cc.addParameter("Mindestsignifikanz", signifikanz);
		cc.addParameter("Limit", limit);
		cc.execute();
		result2 = cc.getResult();
		System.out.println("länge: " + result2.length);
		for (int i = 0; i < result2.length; i++){
			System.out.println(result2[i][1]);
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
