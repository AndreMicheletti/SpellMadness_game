package game.main;

import game.engine.Engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que permite ler e gravar arquivos. É responsável pelos dados de profile e saves
 * @author André Micheletti
 *
 */
public class DataManager {
	
	public static String FOLDER = "res/data/";
	
	/** Iniciado junto com a aplicação, 
	 *  Inicia todas as pastas que não podem faltar
	 */
	public static void initialize() {
		new File(FOLDER).mkdirs();
		if (criarPasta("player/")) {
			gravarArq("player/preferences.spm", "0;");
		}
		criarPasta("saves/");
	}
	
	/** Adiciona conteudo a um arquivo, e se ele não existir, o cria
	 * @param filename nome do arquivo (será adicionado o caminho da pasta automaticamente)
	 * @param contents o conteudo para ser gravado no arquivo
	 * @return se foi possivel gravar o arquivo
	 */
	public static boolean gravarArq(String filename, String contents) {
		filename = FOLDER + filename;
		if (Engine.use_encryption) {
			contents = encryptString(contents);
		}
	    try { 
		     File file = new File(filename);   
		     boolean success = file.createNewFile();  
		     if (success) {
		    	 BufferedWriter out = new BufferedWriter(new FileWriter(filename));  
		    	 out.write(contents);  
		    	 out.close();
		    	 return true;
		     } else {  
		         String arq = lerArq(filename);
		         if ("error".equals(arq)){
		        	 System.out.println("Error when trying to read file. Called by gravarArq");
		        	 return false;
		         }
		         
		         BufferedWriter out = new BufferedWriter(new FileWriter(filename)); 
		         String str = arq + contents;
		    	 out.write(str);  
		    	 out.close();
		    	 return true;
		     }  
		} catch (Exception e) { 
			e.printStackTrace();
			return false;
		}
	}
	
	/** Substitui o conteudo de um arquivo existente
	 * @param filename nome do arquivo (será adicionado o caminho da pasta automaticamente)
	 * @param contents o conteudo para ser gravado no arquivo
	 * @return se foi possivel gravar o arquivo
	 */
	public static boolean substituirArq(String filename, String contents) {
		filename = FOLDER + filename;
		if (Engine.use_encryption) {
			contents = encryptString(contents);
		}
	    try {  
		     File file = new File(filename);   
		     boolean success = file.exists();  
		     if (success) {
		    	 BufferedWriter out = new BufferedWriter(new FileWriter(filename));  
		    	 out.write(contents);  
		    	 out.close();
		    	 return true;
		     } else {  
		         return false;
		     }  
		} catch (IOException e) {  
			e.printStackTrace();
			return false;
		}
	}
	
	/** Retorna o conteudo de um arquivo simples
	 * @param filename nome do arquivo (será adicionado o caminho da pasta automaticamente)
	 * @return o conteudo do arquivo
	 */
	public static String lerArq(String filename) {
		filename = FOLDER + filename;
		File file = new File(filename);   
	    boolean exist = file.exists();
	    if (exist == false){
	    	System.out.println("File "+filename+" does not Exist. Called by Ler Arq");
	    	return "error";}
	    
	    String result = "";
	    try {  
	        BufferedReader in = new BufferedReader(new FileReader(filename));  
	        String str;  
	        while ((str = in.readLine()) != null) {
	        	result += str;
	        }  
	        in.close();
			if (Engine.use_encryption) {
				result = decryptString(result);
			}
	        return result;
	    } catch (IOException e) {
			e.printStackTrace();
	    	return "error";
	    }
	}
	
	/** Retorna um boolean se o arquivo existir
	 * @param filename nome do arquivo (será adicionado o caminho da pasta automaticamente)
	 * @return se o arquivo existe
	 */
	public static boolean existe(String filename) {
		filename = FOLDER + filename;
		File file = new File(filename);   
	    boolean exist = file.exists();
	    return exist;
	}
	
	/** Cria uma pasta
	 * @param caminho o caminho do arquivo  (será adicionado o caminho da pasta automaticamente)
	 * @return se foi possivel criar a pasta
	 */
	public static boolean criarPasta(String caminho) {
		caminho = FOLDER + caminho;
		File dir = new File(caminho);
		boolean result = dir.mkdirs();
		System.out.println("MkDir returned: " + result);
		return result;
	}
	
	/** Deleta uma pasta
	 * @param caminho o caminho do arquivo  (será adicionado o caminho da pasta automaticamente)
	 * @return se foi possivel deletar a pasta
	 */
	public static boolean deletarPasta(String caminho) {
		caminho = FOLDER + caminho;
		File dir = new File(caminho);
		File[] sub = dir.listFiles();    
        for (File f : sub) {    
            if (f.isDirectory()) {
            	System.out.println ("This "+f+" is a directory");    
            } else {    
                f.delete();
            }
        }
		boolean result = dir.delete();
		System.out.println("Delete returned: " + result);
		return result;
	}
	
	/** Retorna uma lista de arquivos no caminho
	 * @param caminho o caminho do arquivo  (será adicionado o caminho da pasta automaticamente)
	 * @return os arquivos listados
	 */
	public static List<File> listarArqs(String caminho) {
		caminho = FOLDER + caminho;
		List<File> arquivos = new ArrayList<File>();   
        File dir=new File(caminho);  
        if (dir.isDirectory()) {    
            File[] sub = dir.listFiles();    
            for (File f : sub) {    
                if (f.isDirectory()) {    
                    System.out.println ("This "+f+" is a directory");    
                } else {    
                    arquivos.add (f);    
                }  
            }  
        } 
        return arquivos;
	}
	
	/** Deleta um arquivo e retorna um boolean
	 * @param filename nome do arquivo (será adicionado o caminho da pasta automaticamente)
	 * @return se foi possivel deletar o arquivo
	 */
	public static boolean deletarArq(String filename) {
		filename = FOLDER + filename;
		File file = new File(filename);
		if (file.exists()) {
			boolean success = file.delete();
			return success;
		} else {
			return false;
		}
	}
	
	/**
	 * Encripta de forma simples uma string
	 * @param s a string a ser encriptada
	 * @return a string encriptada
	 */
	public static String encryptString(String s) {
		String result = "";
		for (char c : s.toCharArray()) {
			result += (char) (c + 1);
		}
		return result;
	}
	
	/**
	 * Desencripta uma string encriptada por esta classe
	 * @param s a string encriptada
	 * @return a string normal
	 */
	public static String decryptString(String s) {
		String result = "";
		for (char c : s.toCharArray()) {
			result += (char) (c - 1);
		}
		return result;
	}
	
	// Métodos para arquivos fora da pasta padrão
	
	/** Adiciona conteudo a um arquivo, e se ele não existir, o cria
	 * @param filename nome do arquivo (não será adicionado o caminho da pasta automaticamente)
	 * @param contents o conteudo para ser gravado no arquivo
	 * @return se foi possivel gravar o arquivo
	 */
	public static boolean gravarArq2(String filename, String contents) {
		if (Engine.use_encryption) {
			contents = encryptString(contents);
		}
	    try { 
		     File file = new File(filename);   
		     boolean success = file.createNewFile();  
		     if (success) {
		    	 BufferedWriter out = new BufferedWriter(new FileWriter(filename));  
		    	 out.write(contents);  
		    	 out.close();
		    	 return true;
		     } else {  
		         String arq = lerArq(filename);
		         if ("error".equals(arq)){
		        	 System.out.println("Error when trying to read file. Called by gravarArq");
		        	 return false;
		         }
		         
		         BufferedWriter out = new BufferedWriter(new FileWriter(filename)); 
		         String str = arq + contents;
		    	 out.write(str);  
		    	 out.close();
		    	 return true;
		     }  
		} catch (Exception e) { 
			e.printStackTrace();
			return false;
		}
	}
	
	/** Substitui o conteudo de um arquivo existente
	 * @param filename nome do arquivo ( não será adicionado o caminho da pasta automaticamente)
	 * @param contents o conteudo para ser gravado no arquivo
	 * @return se foi possivel gravar o arquivo
	 */
	public static boolean substituirArq2(String filename, String contents) {
		if (Engine.use_encryption) {
			contents = encryptString(contents);
		}
	    try {  
		     File file = new File(filename);   
		     boolean success = file.exists();  
		     if (success) {
		    	 BufferedWriter out = new BufferedWriter(new FileWriter(filename));  
		    	 out.write(contents);  
		    	 out.close();
		    	 return true;
		     } else {  
		         return false;
		     }  
		} catch (IOException e) {  
			e.printStackTrace();
			return false;
		}
	}
	
	/** Retorna o conteudo de um arquivo simples
	 * @param filename nome do arquivo (não será adicionado o caminho da pasta automaticamente)
	 * @return o conteudo do arquivo
	 */
	public static String lerArq2(String filename) {
		File file = new File(filename);   
	    boolean exist = file.exists();
	    if (exist == false){
	    	System.out.println("File "+filename+" does not Exist. Called by Ler Arq");
	    	return "error";}
	    
	    String result = "";
	    try {  
	        BufferedReader in = new BufferedReader(new FileReader(filename));  
	        String str;  
	        while ((str = in.readLine()) != null) {
	        	result += str;
	        }  
	        in.close();
			if (Engine.use_encryption) {
				result = decryptString(result);
			}
	        return result;
	    } catch (IOException e) {
			e.printStackTrace();
	    	return "error";
	    }
	}
	
	/** Retorna um boolean se o arquivo existir
	 * @param filename nome do arquivo (não será adicionado o caminho da pasta automaticamente)
	 * @return se o arquivo existe
	 */
	public static boolean existe2(String filename) {
		File file = new File(filename);   
	    boolean exist = file.exists();
	    return exist;
	}
	
	/** Cria uma pasta
	 * @param caminho o caminho do arquivo  (não será adicionado o caminho da pasta automaticamente)
	 * @return se foi possivel criar a pasta
	 */
	public static boolean criarPasta2(String caminho) {
		File dir = new File(caminho);
		boolean result = dir.mkdirs();
		System.out.println("MkDir returned: " + result);
		return result;
	}
	
	/** Deleta uma pasta
	 * @param caminho o caminho do arquivo  (não será adicionado o caminho da pasta automaticamente)
	 * @return se foi possivel deletar a pasta
	 */
	public static boolean deletarPasta2(String caminho) {
		File dir = new File(caminho);
		File[] sub = dir.listFiles();    
        for (File f : sub) {    
            if (f.isDirectory()) {
            	System.out.println ("This "+f+" is a directory");    
            } else {    
                f.delete();
            }
        }
		boolean result = dir.delete();
		System.out.println("Delete returned: " + result);
		return result;
	}
	
	/** Retorna uma lista de arquivos no caminho
	 * @param caminho o caminho do arquivo  (não será adicionado o caminho da pasta automaticamente)
	 * @return os arquivos listados
	 */
	public static List<File> listarArqs2(String caminho) {
		List<File> arquivos = new ArrayList<File>();   
        File dir=new File(caminho);  
        if (dir.isDirectory()) {    
            File[] sub = dir.listFiles();    
            for (File f : sub) {    
                if (f.isDirectory()) {    
                    System.out.println ("This "+f+" is a directory");    
                } else {    
                    arquivos.add (f);    
                }  
            }  
        } 
        return arquivos;
	}
	
	/** Deleta um arquivo e retorna um boolean
	 * @param filename nome do arquivo (não será adicionado o caminho da pasta automaticamente)
	 * @return se foi possivel deletar o arquivo
	 */
	public static boolean deletarArq2(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			boolean success = file.delete();
			return success;
		} else {
			return false;
		}
	}
}
