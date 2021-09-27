import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Pair{

    Pair(String first,String second){
        this.first = first;
        this.second = second;
    }

    public String first;
    public String second;
}

public class Parser {
    public static Map<String, ArrayList<String>> rules = new HashMap<>();
    public static Set<String> left_set = new LinkedHashSet<>();
    public static ArrayList<String> left_data = new ArrayList<>();
    public static ArrayList<String> left = new ArrayList<>();
    public static ArrayList<String> right = new ArrayList<>();
    public static ArrayList<String> terminal = new ArrayList<>();
    public static ArrayList<String> neterminal = new ArrayList<>();
    public static Map<String, Set<String>> FIRST = new HashMap<>();
    public static Map<String, Set<String>> FOLLOW = new HashMap<>();
    public static ArrayList<String> leksems = new ArrayList<>();


    public void Parser(ArrayList<String> list){
        leksems.addAll(list);
    }
    public  void Parsing(ArrayList<String> scanner_leskems) throws IOException {
        leksems.addAll(scanner_leskems);
        File file = new File("rules.txt");
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        String s = reader.readLine();
        neterminal.addAll(left);
        String left_str = "";
        char[] chars = s.toCharArray();
        while (s != null) {

            for (int i = 0; chars[i] != '-'; i++) {
                left_str += chars[i];
            }

            rules.put(left_str, new ArrayList<String>());
            left_data.add(left_str);
            left_set.add(left_str);
            s = reader.readLine();
            if (s != null) chars = s.toCharArray();
            left_str = "";
        }
        AddRight();
        left.addAll(left_set);
        neterminal.addAll(left_set);
        for (int i = 0; i < rules.size(); i++) {
            for (int j = 0; j < rules.get(left.get(i)).size(); j++) {
                System.out.println(left.get(i) + "->" + rules.get(left.get(i)).get(j));
            }
        }
        AddTerminal();
        CreateFirst();
        Create_Follow();
        String str = "";
        ArrayList<String> strings = new ArrayList<>();

        Stack<String> stack = new Stack<>();
        for (int i = 0; i < leksems.size(); i++) {
            str = leksems.get(i);
            while (str.contains(" ")) {
                strings.add(str.substring(0, str.indexOf(" ")));
                str = (new StringBuilder(str)).insert(str.indexOf(" "), "&").toString();
                str = str.replace(str.substring(0, str.indexOf(" ") + 1), "");
            }
            strings.add(str);
            boolean result = Iteration_String(strings,stack,-1);
            str = leksems.get(i);
            System.out.println(str + " - " + result);
            if(!result){

                Pattern pattern = Pattern.compile(".* .*;");
                Matcher matcher = pattern.matcher(str);
                if(matcher.find()){
                    System.out.println("Задан нейзвестный тип данных, возможно вы имелли вииду (int,float,bool)");
                }
                pattern = Pattern.compile("\\w* [int float bool]+");
                matcher = pattern.matcher(str);
                if(matcher.find() && !str.contains(";") && matcher.start() == 0 && matcher.end() == str.length()){
                    System.out.println("Пропущена ;");
                }
            }
            stack.clear();
            strings.clear();
        }




    }

    public static void CreateFirst() {

        for (int i = 0; i < left.size(); i++) {
            FIRST.put(left.get(i),new LinkedHashSet<>());
        }



        for (int i = 0; i < FIRST.size(); i++) {
            for (int j = 0; j < left_data.size(); j++) {

                    if(right.get(j).indexOf(' ') >=0)
                        FIRST.get(left_data.get(j)).add(right.get(j).substring(0, right.get(j).indexOf(" ")));
                    else
                        FIRST.get(left_data.get(j)).add(right.get(j));

            }
        }

        while(!Proverka_First()){
            boolean End = false;
            for (int i = 0; i < left.size(); i++) {
                for (String first : FIRST.get(left.get(i))) {
                    for (int k = 0; k < neterminal.size(); k++) {
                        if(first.equals(neterminal.get(k))){
                            FIRST.get(left.get(i)).remove(neterminal.get(k));
                            Change_First(neterminal.get(k),left.get(i));
                            End = true;
                        }
                    }
                    if(End) break;
                }
            }
        }
    }


    static void Create_Follow(){
        for (int i = 0; i < left.size(); i++) {
            FOLLOW.put(left.get(i),new LinkedHashSet<>());
        }
        String s;

        for (int i = 0; i < left.size(); i++) {
            for (int j = 0; j < right.size(); j++) {
                FIRST_STEP_FOLLOW(right.get(j), left.get(i));
            }
        }

        SECOND_STEP_FOLLOW();
     }
     static boolean Iteration_String( ArrayList<String> leksem, Stack<String> stack,int ex_index){
        int index = 0;

//        if(stack.size() > 50){
//            System.out.println("false");
//            System.exit(0);
//        }

         if(leksem.isEmpty()){
             if(!stack.empty()){
                 while(!stack.empty()){
                     if(!Proverka_Terminal(stack.pop())){
                         return false;
                     }
                 }
             }
             return true;



         }else if(stack.empty()){
             ArrayList<String> save_leksem = new ArrayList<>();
            for(var lef : left){
                for(var temp : FIRST.get(lef)){
                    if(leksem.get(0).equals(temp)){
                        stack.add(lef);
                        save_leksem.addAll(leksem);
                        if(Iteration_String(leksem,stack,-1)){
                            return true;
                        }else{
                            while(!stack.empty()) stack.pop();
                            while(!leksem.isEmpty()) leksem.remove(0);

                            leksem.addAll(save_leksem);
                            while(!save_leksem.isEmpty()) save_leksem.remove(0);
                        }
                    }
                }
            }
            return false;


        }else if(Proverka_First(stack.peek(),leksem.get(0),index)>=0) {
            while (Proverka_First(stack.peek(), leksem.get(0), index)>=0) {
                index = Proverka_First(stack.peek(), leksem.get(0), index);
                if(ex_index == index) {
                    index++;
                    continue;
                }

                Stack<String> stack_save = new Stack<>();
                stack_save.addAll(stack);
                stack.pop();
                if(stack.size() > 0 && leksem.size() > 1) {
                    if (!Proverka_Terminal(stack.peek()) && !leksem.get(1).equals(stack.peek())) {
                        return false;
                    }
                }
                String newStack = right.get(index);
                Stack<String> stack1 = new Stack<>();

                ArrayList<String> list_save = new ArrayList<>();
                list_save.addAll(leksem);

                stack1.addAll(stack);
                while(!stack.empty()) stack.pop();

                while (newStack.contains(" ")) {
                    stack.add(newStack.substring(0, newStack.indexOf(" ")));
                    newStack = (new StringBuilder(newStack)).insert(newStack.indexOf(" "), "&").toString();
                    newStack = newStack.replace(newStack.substring(0, newStack.indexOf(" ") + 1), "");
                }
                if (stack1.size() > 0) if(newStack.equals(stack1.peek())) return false;
                stack.add(newStack);
             while(!stack1.empty()) stack.add(stack1.pop());
             Collections.reverse(stack);



             if (Iteration_String(leksem, stack,index)) {
                 return true;
             }else {
                 while (!stack.empty()) stack.pop();
                 while (!leksem.isEmpty()) leksem.remove(0);
                 stack.addAll(stack_save);
                 leksem.addAll(list_save);
                 index += 1;
             }
         }
         return false;
     }



        else if(stack.peek().equals(leksem.get(0))) {
        stack.pop();
        leksem.remove(0);
        return Iteration_String(leksem, stack,-1);


    }else if(Proverka_Second(stack,leksem.get(0))) {
        stack.pop();
        stack.pop();
            leksem.remove(0);
            return Iteration_String(leksem, stack,-1);
        }
        return false;
     }

     static boolean Proverka_Second(Stack<String> stack, String leksema){
        Stack<String> stack1 = new Stack<>();
        stack1.addAll(stack);
        String first_element = stack1.pop();

        if(Proverka_Terminal(first_element)){
            if(stack1.empty()) return false;
                for(var temp : FOLLOW.get(first_element)){
                    if(stack1.peek().equals(temp) && leksema.equals(stack1.peek())){
                        return true;
                    }

            }
        }
        return false;

     }

     static boolean Proverka_Terminal(String s){
        for (var lef : left){
            if(lef.equals(s)) return true;
        }
        return false;
     }



     static int Proverka_First(String s, String leksema, int index){
        String righ;
        for(int i = index; i < left_data.size();i++){
            if(right.get(i).contains(" ")){
                righ = right.get(i).substring(0,right.get(i).indexOf(" "));
            }else{
                righ = right.get(i);
            }
            if(s.equals(left_data.get(i)) && righ.equals(leksema)){
                index = i;
                return index;
            }else if(s.equals(left_data.get(i)) && Proverka_Terminal(righ)){
                    for (var temp : FIRST.get(righ)) {
                        if(temp.equals(leksema)) {
                            index = i;
                            return index;
                        }
                }
            }
        }


        return -1;
     }

    static void SECOND_STEP_FOLLOW(){
        boolean isBreak = false;
        int count = 0;
        Map<String,ArrayList<String>> map = new HashMap<>();
        for(var lef : left) map.put(lef,new ArrayList<>());
        for (int i = 0; i < right.size(); i++) {
                    String rul = right.get(i);
                    while(rul.indexOf(' ') >= 0) {
                        rul = rul.replace(rul.substring(0, rul.indexOf(" ")) + " ", "");
                    }

                    for(var term : left){
                        if(term.equals(rul)){
                            count = FOLLOW.get(term).size();
                            for(var follow : FOLLOW.get(left_data.get(i))) FOLLOW.get(term).add(follow);
                            if(count != FOLLOW.get(rul).size()){
                                i = 0;
                            }
                            break;
                        }
                    }
                    //if(isBreak) break;
        }


        for (int i = 0; i < left.size(); i++) {
            isBreak = false;
            boolean isDelete = true;
                for(var foll : FOLLOW.get(left.get(i))) {
                    for (var term : left) {
                        if (foll.equals(term)) {
                            for(var first : FIRST.get(foll)) {
                                FOLLOW.get(left.get(i)).add(first);
                            }
                            map.get(left.get(i)).add(foll);
                            for(var follow : FOLLOW.get(foll)) {
                                for (var m : map.get(left.get(i))) {
                                    if(follow.equals(m)) isDelete = false;
                                }
                                if(isDelete) {
                                    isDelete = true;
                                    FOLLOW.get(left.get(i)).add(follow);
                                }
                            }
                            FOLLOW.get(left.get(i)).remove(foll);
                            isBreak = true;
                            i = 0;
                            break;
                            }
                        }
                    if(isBreak) break;
                    }
                }
        }


    static void FIRST_STEP_FOLLOW(String right, String symbol) {
        String rig = right;
        String s;
        boolean isTerminal;
        while (!rig.equals("")) {
            isTerminal = false;
            if (rig.indexOf(' ') >= 0) {
                s = rig.substring(0, rig.indexOf(" "));
                rig = (new StringBuilder(rig)).insert(rig.indexOf(" "), "&").toString();
                String delete =  rig.substring(0, rig.indexOf(" ")+ 1);
                rig = rig.replace( delete, "");
            } else {
                rig = "";
                continue;
            }
            for (var term : left) {
                if (s.equals(term)) {
                    isTerminal = true;
                    break;
                }
            }

            if(isTerminal && !rig.equals("\0") && symbol.equals(s)){
                if (rig.indexOf(' ') >= 0) {
                    s = rig.substring(0, rig.indexOf(" "));
                    rig = rig.replace(s + " ", "");
                    FOLLOW.get(symbol).add(s);
                }else{
                    FOLLOW.get(symbol).add(rig);
                }
            }

        }
    }

    static void Change_First(String neterminal, String left){

            for(String first : FIRST.get(neterminal)) {
                if(!neterminal.equals(first)) {
                    FIRST.get(left).add(first);
                }
            }

    }

    static boolean Proverka_First(){

        for (int i = 0; i < left.size(); i++) {
            for(String first : FIRST.get(left.get(i))) {
                for (int k = 0; k < neterminal.size(); k++) {
                    if(first.equals(neterminal.get(k))) return false;
                }
            }

        }
        return true;
    }

    public static void AddTerminal() {
        boolean isTerminal;
        int index;
        for (var righ : right) {
            String s;
            String rig;
            rig = righ;
            while (!rig.equals("")) {
                isTerminal = true;
                if (rig.indexOf(' ') >= 0) {
                    s = rig.substring(0, rig.indexOf(" "));
                    rig = rig.replace(s + " ", "");
                } else {
                    s = rig;
                    rig = rig.replace(s, "");
                }

                for (var term : left) {
                    if (s.equals(term)) {
                        isTerminal = false;
                        break;
                    }
                }
                if (isTerminal) terminal.add(s);
            }
        }
    }


    public static void AddRight() throws IOException {
        File file = new File("rules.txt");
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        String s = reader.readLine();
        char[] chars = s.toCharArray();
        String left_str = "";
        while (s != null) {
            for (int i = 0; chars[i] != '-'; i++) {
                left_str += chars[i];
            }
            String right_line = "";
            for (int i = left_str.length() + 2; i < s.length(); i++) {
                right_line += chars[i];
            }
            String str;
            String rig = right_line;
            while (!rig.equals("")) {

                if (rig.indexOf(' ') >= 0) {
                    str = rig.substring(0, rig.indexOf(" "));
                    rig = rig.replace(str + " ", "");
                } else {
                    str = rig;
                    rig = rig.replace(str, "");
                }

                rules.get(left_str).add(str);
            }
            right.add(right_line);
            s = reader.readLine();
            if (s != null) chars = s.toCharArray();
            left_str = "";
        }
    }
}


