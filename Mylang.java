import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * 
 * @author Gokberk Erust ---->
 *this program simply compiles
 * mylang to assembly language
 *To run it properly make sure you give
 *to args to command line that first one indicates the code in mylang
 *and the second is the output file
*/
public class Mylang {
	/**
	 * @param countBegin 	counts the number of begins
	 * @param countEnd		counts the number of ends
	 * @param countPrnth1 	counts the "(" 
	 * @param countPrnth2	counts the ")"
	 * @param labelNum		keeps the number of labels that used
	 * @param names         stores the id's
	 */
	static int countBegin,countEnd,countPrnth1,countPrnth2;
	static int labelNum = 1 ;
	static ArrayList<String> names = new ArrayList<String>(); ;
	/**
	 * 
	 * @param args args[0] for the input file and args[1] for the output file
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args)throws FileNotFoundException{
		
		if(args.length != 2){
			error("invaild argument");
		}
		/**
		 *@param input     stores the whole contents of the given input file as string
		 *@param output    stores the  strings that will written in output file
		 */
		String input = "" ;
		String output ="";
		File inFile = new File(args[0]);
		Scanner s = new Scanner(inFile);
		countPrnth1= countPrnth2 = countBegin = countEnd = 0 ;
		
		/**
		 * reads the file
		 */
		while(s.hasNext()){
			input += s.next() + " ";
		}
		s.close();
		output = abstractToAssembly(stm(input)) + writeForAll() + variables(names);
		try {
			/**
			 * writing to output file
			 */
			PrintWriter outFile = new PrintWriter(args[1]);
            outFile.println(output);
            outFile.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	
	}
	/**
	 * 
	 * @param x  	string that is a statement according to given language
	 * @return	    the abstract codes of given language or errors
	 */
	public static String stm(String x){
		if(x.equalsIgnoreCase(null)){
			error("'stmt' cannot be null");
			return null;
		}
		Scanner s = new Scanner(x);
		String temp ="";
		String d = "";
		while(s.hasNext()){
			temp = s.next();

			 if(temp.equals("print")){
				 while(s.hasNext()){
					d += s.next() + " ";
				 }
				 if(d.isEmpty())
					 error("after 'print' there is a expression expected ");
				 s.close();
				 return expr(d) + " print \n";
			 }else if(temp.equals("while")){
				 while(s.hasNext()){
					 temp = s.next();
					 if(temp.equals("do")){
						 break;
					 }
					 d += temp + " ";
				 }
				 if(d.isEmpty())
					 error("after 'while' there is a expression expected ");
				 if(!temp.equals("do"))
					 error("afte 'while' 'do' expected");
				 String b = "";
				 while(s.hasNext()){
						b += s.next()+ " ";
					}
				
				int a  = labelNum ;
				 int y = a +1 ;
				 labelNum += 2 ;
				 s.close();
				return " lL " +a + "\n" +expr(d) +" jifL "+ y+ "\n"+stm(b) + " jaL " + a + "\n lL " + y + "\n";
			}else if (temp.equals("if")){
				while(s.hasNext()){
					 temp = s.next();
					 if(temp.equals("then")){
						 break;
					 }
					 d += temp + " ";
				 }
				 if(d.isEmpty())
					 error("after 'if' there is a expression expected ");
				 if(!temp.equals("then"))
					 error("after 'if' 'then' expected");
				 String b = "";
				 while(s.hasNext()){
						b += s.next()+ " ";
					}
				
				 int a =labelNum;
				 s.close();
				return expr(d)+ " jifL "+ a + "\n" + stm(b)+ " lL " + a + "\n";
			}else if(temp.equals("begin")){
				countBegin++;
				while(s.hasNext()){
					temp = s.next();
					if(temp.equals("begin")){
						countBegin++;
						d += temp + " ";
					}else if(temp.equals("end")&& countBegin != countEnd + 1){
						countEnd++;
						d += temp + " ";
						//return opt_stmt(d);
					}else if(temp.equals("end")&& countBegin == countEnd + 1){
						countEnd++;
						s.close();
						return opt_stmt(d);
					}else{
						d += temp + " ";
					}
				}
				error("missing 'end' statement");
			}else if(temp.equals("read")){
				//String m = "";
				d = s.next();
				names.add(d);
				s.close();
				return " read " + d + "\n";
			}else if(temp.equals(";")){
				
			}else {
				if(s.hasNext()){
					if(s.next().equals("=")){
						while(s.hasNext()){
							d += s.next() + " ";
						}
						if(d.isEmpty())
							error("'expr' cannot be null");
					}if(!temp.isEmpty()){
						 names.add(temp);
					}
					s.close();
					return "push-addr-var " +temp +"\n " + expr(d) + " assign \n";
				}else{
					error("'=' is expected");
				}
			}
		}
		s.close();
		return null;
	}
	/**
	 * 
	 * @param x				Strings that is expressions according to given grammar
	 * @return				division of a given expression to term or moreterms
	 */
	private static String expr(String x) {
		Scanner s = new Scanner(x);
		if(x.equalsIgnoreCase(null)){
			error("'expr' cannot be null");
			s.close();
			return null;
		}
		String d = "";
		String temp = "" ;
		boolean needNext = true ;
		while(s.hasNext() && needNext ){
			temp = s.next();
			d += temp + " " ;
			needNext = false ;
			
			if(temp.equals("(")){
				countPrnth1++;
				needNext = true;
			}else if(temp.equals(")")){
				if(countPrnth1 - countPrnth2 > 1)
					needNext=true ;
				countPrnth2++;
				
			}else if(temp.equals("+") || temp.equals("-")){
				if(countPrnth1 == countPrnth2){
					needNext = false;
				}else{
					needNext = true;
				}
			}else{
				needNext = true ;
			}
		}
		if(s.hasNext()){
			temp += " ";
			String b = temp + " ";
			while(s.hasNext()){
				b += s.next()+ " ";
			}
			s.close();
			 return term(d) + moreterms(b);
		}else{
			s.close();
			return term(d);
		}
	}
	/**
	 * 
	 * @param x				Strings that is moreterms according to given grammar
	 * @return				terms and operators or terms, operators and moreterms
	 */
	private static String moreterms(String x) {
		if(x.isEmpty()){
			return "";
		}
		Scanner s = new Scanner(x);
		String d ,temp,operator ;
		d ="";
		temp = "";
		operator = s.next();
		/**
		 * @param needNext 			controls the while loop and checks if it necessary to read more from buffer
		 */
		boolean needNext = true ;
		while(s.hasNext() && needNext ){
			temp = s.next();
			d += temp + " " ;
			needNext = false ;
			
			if(temp.equals("(")){
				countPrnth1++;
				needNext = true;
			}else if(temp.equals(")")){
				if(countPrnth1 - countPrnth2 > 1)
					needNext=true ;
				countPrnth2++;
				
			}else if(temp.equals("+") || temp.equals("-")){
				if(countPrnth1 == countPrnth2){
					needNext = false;
				}else{
					needNext = true;
				}
			}else{
				needNext = true ;
			}
		}
		if(s.hasNext()){
			temp += " ";
			String b = temp + " ";
			while(s.hasNext()){
				b += s.next()+ " ";
			}
			s.close();
			return term(d) +  operator + "\n" +moreterms(b);
		}else{
			s.close();
			return term(d)+ operator + "\n" ;
		}
	}
	private static String term(String x) {
		if(x.equalsIgnoreCase(null)){
			error("'term' cannot be null");
			return null;
		}
		Scanner s = new Scanner(x);
		String d = "";
		String temp = "" ;
		boolean needNext = true ;
		while(s.hasNext() && needNext ){
			temp = s.next();
			d += temp + " " ;
			needNext = false ;
			
			if(temp.equals("(")){
				countPrnth1++;
				needNext = true;
			}else if(temp.equals(")")){
				if(countPrnth1 - countPrnth2 > 1)
					needNext=true ;
				countPrnth2++;
				
			}else if(temp.equals("*") || temp.equals("/") || temp.equals("%")){
				if(countPrnth1 == countPrnth2){
					needNext = false;
				}else{
					needNext = true;
				}
			}else{
				needNext = true ;
			}
		}
		if(s.hasNext()){
			temp += " ";
			String b = temp + " ";
			while(s.hasNext()){
				b += s.next()+ " ";
			}
			s.close();
			 return factor(d) + morefactors(b);
		}else{
			s.close();
			return factor(d);
		}
	}
	private static String morefactors(String x) {
		if(x.isEmpty()){
			return "";
		}
		Scanner s = new Scanner(x);
		String d ,temp,operator ;
		d ="";
		temp = "";
		operator = s.next();
		boolean needNext = true ;
		while(s.hasNext() && needNext ){
			temp = s.next();
			d += temp + " " ;
			needNext = false ;
			
			if(temp.equals("(")){
				countPrnth1++;
				needNext = true;
			}else if(temp.equals(")")){
				if(countPrnth1 - countPrnth2 > 1)
					needNext=true ;
				countPrnth2++;
				
			}else if(temp.equals("*") || temp.equals("/") || temp.equals("%")){
				if(countPrnth1 == countPrnth2){
					needNext = false;
				}else{
					needNext = true;
				}
			}else{
				needNext = true ;
			}
		}
		if(s.hasNext()){
			temp += " ";
			String b = temp + " ";
			while(s.hasNext()){
				b += s.next()+ " ";
			}
			s.close();
			return factor(d) +operator + " \n" + morefactors(b);
		}else{
			s.close();
			return factor(d)+ operator + "\n" ;
		}
	}
	/**
	 * 
	 * @param x					Strings that is factors according to given grammar
	 * @return					either the value of integer or the id
	 */
	private static String factor(String x) {
		Scanner s = new Scanner(x);
		String temp = s.next();
		if(temp.equals("(")){
			String d ="";
			while(s.hasNext()){
				temp = s.next();
				if(temp.equals(")")){
					break ;
				}else{
					d += temp + " ";
				}
			}
			if(!temp.equals(")")){
				error("')' expected");
			}
			s.close();
			return expr(d);
		}else if(isNumber(temp)){
			s.close();
			return " push-num " + temp + " ";
		}else {
			s.close();
			return " push-val-var " + temp + " ";
			
		}
	}
	/**
	 * 
	 * @param x 			takes a string and checks whether it is a number
	 * @return				true if x is number
	 */
	private static boolean isNumber(String x) {
		try{
			Integer.parseInt(x);
		}catch (Exception e){
			return false ;
		}
		return true;
	}
	/**
	 * 
	 * @param x 		Strings that is opt_stmts according to given grammar
	 * @return			stm list or null
	 */
	public static String opt_stmt(String x){
		if(!x.equals("")){
			return stmt_list(x);
		}else
			return "" ;
	}
	/**
	 * 
	 * @param x			Strings that is stm_lists according to given grammar
	 * @return			stmts or stm_list
	 */
	private static String stmt_list(String x) {
		Scanner s = new Scanner(x);
		if(x.equalsIgnoreCase(null)){
			error("stmt_list cannot be null");
			s.close();
			return null;
		}
		String d = "";
		String temp = "" ;
		boolean needNext = true ;
		while(s.hasNext() && needNext ){
			temp = s.next();
			d += temp + " " ;
			needNext = false ;
			
			if(temp.equals("begin")){
				countBegin++;
				needNext = true;
			}else if(temp.equals("end")){
				if(countBegin - countEnd > 1)
					needNext=true ;
				countEnd++;
				
			}else if(temp.equals("if")){
				needNext = true;
				
			}else if(temp.equals("while")){
				needNext = true;
				
			}else if(temp.equals(";")){
				if(countBegin != countEnd){
					needNext = true;
				}else{
					needNext = false ;
				}
			}else if(temp.equals("read")){
				needNext = true ;
			}else{
				needNext = true ;
			}
			
		}
		
		if(!needNext && countBegin != countEnd)
			error("Missing end or begin");
		
		if(s.hasNext()){
			String b = "";
			while(s.hasNext()){
				b += s.next()+ " ";
			}
			s.close();	
			return stm(d) + stmt_list(b);
		}else{
			s.close();
			return stm(d);
		}
	}
	/**
	 * 
	 * @param x				the message while the program detects an error according to given grammer
	 */
	private static void error(String x) {
		System.err.println("Error: " + x);
		System.exit(1);
	}
	/**
	 * 
	 * @return		Myread Myprint fucntions according to their assembly codes
	 */
	public static String writeForAll(){
		String s1 ="myread: \n MOV  CX,0 \n" ;
		String s2 ="morechar: \n mov  ah,01h \n int  21H \n mov  dx,0 \n mov  dl,al \n mov  ax,cx \n cmp  dl,0D \n je myret \nsub  dx,48d \n mov  bp,dx \n mov  ax,cx \n mov  cx,10d \n mul  cx \n add  ax,bp \n mov  cx,ax \n jmp  morechar\n";                  
		String s3 ="myret:\n ret \n";
		String s4 ="myprint: \n mov    si,10d \n xor    dx,dx\n push   ' '    ; push newline  \n mov    cx,1d \n";
		String s5 ="nonzero: \n div    si \n add    dx,48d \n push   dx \n inc    cx \n xor    dx,dx \n cmp    ax,0h \n jne    nonzero \n";
		String s6 ="writeloop: \n pop    dx \n mov    ah,02h \n int    21h \n dec    cx \n jnz    writeloop \n ret\n";
		return s1 + s2 + s3 + s4 + s5 + s6 ;
	}
	/**
	 * 
	 * @param x					abstract codes written in a string
	 * @return					assembly codes
	 */
	public static String abstractToAssembly(String x){
		String out = "code segment \n";
		Scanner s= new Scanner(x);
		while(s.hasNextLine()){
			String temp = "";
			temp = s.nextLine();
			Scanner r = new Scanner(temp);
			while(r.hasNext()){
				String temp2 = "";
				temp2 = r.next();
				if(temp2.equals("push-addr-var")){
					out += "PUSH offset v" + r.next() + "\n";
				}else if(temp2.equals("assign")){
					out +="POP AX \nPOP BX \nMOV [BX],AX \n";
				}else if(temp2.equals("push-val-var")){
					out += "PUSH v" + r.next() + " W \n";
				}else if(temp2.equals("push-num")){
					out += "PUSH " + r.next() + "\n" ;
				}else if(temp2.equals("*")){
					out += "POP CX \nPOP AX \nMULT CX \nPUSH AX \n";
				}else if(temp2.equals("+")){
					out += "POP CX \nPOP AX \nADD AX,CX  \nPUSH AX \n";
				}else if(temp2.equals("-")){
					out += "POP CX \nPOP AX \nSUB AX,CX  \nPUSH AX \n";
				}else if(temp2.equals("/")){
					out += "MOV DX,0 \nPOP CX \nPOP AX \nDIV CX \nPUSH AX \n";
				}else if(temp2.equals("%")){
					out += "MOV DX,0 \nPOP CX \nPOP AX \nDIV CX \nPUSH DX \n";
				}else if(temp2.equals("print")){
					out += "POP AX \nCALL myprint \n";
				}else if(temp2.equals("read")){
					out += "CALL myread \nMOV v" + r.next() + ",CX \n";
					
				}else if(temp2.equals("jifL")){
					out += "POP AX \nCMP AX,O \nif z JMP LABL" + r.next() + "\n";				
				}else if(temp2.equals("jaL")){
					out += "POP AX \nPOP BX \nMOV [BP],AX \nJPM LABL" + r.next() + "\n";
				}else if(temp2.equals("lL")){
					out += "LABL" + r.next() + ": \n";
				}
			}
			r.close();
		}
		s.close();
		return out + "int 20h \n" ;
	}
	/**
	 * 
	 * @param a					the lists of variables
	 * @return					as a string of variables without any duplicate
	 */
	public static String variables(ArrayList<String> a ){
		String variables = ";;;;;; variables ;;;; \n";
		String rmvDuplicate = "" ;
		for(int i = 0 ; i < names.size() ; i++){
			if(!rmvDuplicate.contains(names.get(i))){
				rmvDuplicate += names.get(i);
				variables += "v" + names.get(i)+ "\t dw ? \n";
			}
		}
		return variables;
	}
}
