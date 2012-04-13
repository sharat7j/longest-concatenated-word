package com.aspera.pack;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;
import javax.management.monitor.CounterMonitorMBean;
import weka.core.Trie;
import weka.core.tokenizers.WordTokenizer;

public class lcw {
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
		SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
				new Comparator<Map.Entry<K,V>>() {
					@Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
						int res =  e1.getValue().compareTo(e2.getValue());
						return res != 0 ? res : 1; // Special fix to preserve items with equal values
					}
				}
				);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
	//maintain a  lookup table to check if the word is present in test file 
	private HashMap<String,Boolean> lookup=new HashMap<String,Boolean>() ;
	private Trie t=new Trie();
	private String longestconcatWord;
	private int countWordsInConcat;
	String getLongestconcatWord()
	{
		return longestconcatWord;
	}
	int getCountWordsInConcat()
	{
		return countWordsInConcat;
	}
	boolean concatCanForm(String word,boolean firstCall)
	{
		//check if its the firstCall and if the word is already present in the lookup table. return the table value for the word
		if(!firstCall && lookup.containsKey(word))
		{
			return lookup.get(word);
		}
		if (firstCall)
		{
			t.remove(word);
		}
		//from n=word.length down to 1 check if different the word is concatenated using already present words in the trie
		//if we reach end of loop then we shall conclude that the word is not a concatenated word and return false
		for(int i=word.length()-1;i>=0;i--)
		{
			if(t.contains(word.substring(0, i+1))) //if substring is in trie
			{
				//and if entire word param is in trie or (substring and) remainder is in trie
				if((i==word.length()-1)||concatCanForm(word.substring(i+1,word.length()), false))
				{
					countWordsInConcat+=1;
					lookup.put(word, true);
					if(firstCall) t.add(word);
					return true;			

				}
			}
		}

		lookup.put(word, false);
		if (firstCall) t.add(word); 

		return false;
	}
	
	
	public void initialTriePopulation(LinkedHashMap<String,Integer> lengthMap)
	{

		for(String key:lengthMap.keySet())
		{
			t.add(key);
		}

	}
	
	
	public int findLongestConcatWord(LinkedHashMap<String,Integer> lengthMap)
	{
		int flag=0;

		List<Entry<String,Integer>> lengthMapList = new ArrayList(lengthMap.entrySet());
		//traverse longest to smallest word 
		for(int i = lengthMapList.size() -1; i >= 0 ; i --)
		{
			countWordsInConcat=0;
			if(concatCanForm(lengthMapList.get(i).getKey(),true))
			{
				longestconcatWord=lengthMapList.get(i).getKey();
				flag=1;
				return flag;
			}
		}
		return flag;

	}
	public void findWord(String filePath)
	{
		try{
			FileInputStream f = new FileInputStream(filePath);
			byte[] buffer = new byte[(int) new File(filePath).length()];
			f.read(buffer);
			String text=new String(buffer);
			StringTokenizer st = new StringTokenizer(text);
			Map<String,Integer> wordList = new HashMap<String,Integer>();
			//insert tokens,token.length into hashmap
			while(st.hasMoreTokens())
			{
				String token=st.nextToken();
				//System.out.println(token);

				wordList.put(token,token.length());

			}

			System.out.println("wordlist");
			System.out.println(wordList);

			LinkedHashMap<String, Integer> lengthMap=new LinkedHashMap<String,Integer>();

			//insert the entries sorted by value into linkedhashmap
			for (Map.Entry<String, Integer> entry  : entriesSortedByValues(wordList)) {

				lengthMap.put(entry.getKey(), entry.getValue());

			}				

			//Initial Trie Population
			initialTriePopulation(lengthMap);
			//check if a concatenated word actually exists using a flag
			findLongestConcatWord(lengthMap);
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

	}

	public static void main(String args[]) throws Exception 
	{
		String filePath="testfile";
		lcw findLongest=new lcw();
		long start=System.currentTimeMillis();
		findLongest.findWord(filePath);
		if(findLongest.getLongestconcatWord()!=null)
		{
			System.out.println("longest concatenated word is="+findLongest.getLongestconcatWord());
			System.out.println("length of the word="+findLongest.getLongestconcatWord().length());
			System.out.println("number of words in it="+findLongest.getCountWordsInConcat());
		}
		else System.out.println("no concatenated word present in testfile");
		long end=System.currentTimeMillis();
		System.out.println("time taken="+(end-start)+" ms");
		  

	}
}