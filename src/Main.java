import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Main {
    private static List<List<Leksema>> leksems = new ArrayList<>();
    private static ArrayList<String> source = new ArrayList<>();


    private static final List<String> operations_connection = new ArrayList<>(Arrays.asList("< >","<=",">="," <"," >"," ="));
    private static final List<String> operations_sum = new ArrayList<>(Arrays.asList("\\+","\\-","or"));
    private static final List<String> operations_multiplication = new ArrayList<>(Arrays.asList("\\*", "\\/", "and"));
    private static final List<String> operations_unary = new ArrayList<>(Arrays.asList("not"));

    private static final List<String> operations_structure = new ArrayList<>(Arrays.asList("program var","begin","end","\\{;","} end."));
    private static final List<String> operations_type = new ArrayList<>(Arrays.asList("int", "float", "bool"));
    private static final List<String> operations_operators = new ArrayList<>(Arrays.asList("ass","then","else","for","to","do","while",
    "read","write"));
    private static final List<String> operations_comments = new ArrayList<>(Arrays.asList("\\{","\\}"));


    private static final List<String> separators = new ArrayList(Arrays.asList("\\.", ";", ",", ":"));
    private static  List<List<String>> symbChains = new ArrayList(Arrays.asList( separators,operations_connection,operations_sum,
            operations_multiplication,operations_unary,operations_structure,operations_type,operations_operators,operations_comments));

    public static List<ArrayList<String>> different_symbols = new ArrayList<>();


    public static ArrayList<String> getList(String str){
        String s = "";
        ArrayList<String> Mylist = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            while(str.toCharArray()[i] != ' '){

                s += str.toCharArray()[i];
                if(i+1<str.length()){ i++; } else {break;}
                if(str.toCharArray()[i] == '\0') break;
            }
            if (!s.equals("")) Mylist.add(s);
            s = "";
        }
        return Mylist;
    }


    public static void getLexems(String source, int index){
        Pattern pattern=null;
        String text = source;                      // передаем строку, которую надо разбить на лексемы
        int tableID = 0;                           // идентификатор таблицы
        String different_symbol = source;
        String str;
        boolean flag = false;
        leksems.add(new ArrayList<>());
        int count = 0;
        for (int j = 0; j < symbChains.size() - index;j++) {

            for (var i = 0; i <= symbChains.get(j).size() - 1 ; i++) {
                    //компилируем паттерн из табличных цепочек символов
                    switch (tableID) {
                        case 0:
                        case 1:
                        case 2:
                        case 8:
                            pattern = Pattern.compile("(" + symbChains.get(j).get(i) + "\\.|" +
                                    symbChains.get(j).get(i) + ";|" +
                                    symbChains.get(j).get(i) + ",|" +
                                    symbChains.get(j).get(i) + ":|" +
                                    symbChains.get(j).get(i) + " |" +
                                    symbChains.get(j).get(i) + "\n|" +
                                    symbChains.get(j).get(i) + "$)");
                            break;
                        default:
                            pattern = Pattern.compile("\\b(" + symbChains.get(j).get(i) + "\\.\\b|\\b" +
                                    symbChains.get(j).get(i) + "\\b;|\\b" +
                                    symbChains.get(j).get(i) + "\\b,|\\b" +
                                    symbChains.get(j).get(i) + "\\b:|\\b" +
                                    symbChains.get(j).get(i) + "\\b |\\b" +
                                    symbChains.get(j).get(i) + "\n|" +
                                    symbChains.get(j).get(i) + "$)");
                            break;
                    }

                    //сканируем строку
                    Matcher matcher = pattern.matcher(text);
                    //В цикле перебираем результат сканирования и выводим информацию о лексемах
                    while (matcher.find()) {
                        int start = matcher.start();
                        int end = 0;

                        switch (tableID) {
                            default:
                                if (matcher.end() != source.length()) {
                                    end = matcher.end() - 1;
                                } else {
                                    end = matcher.end();
                                }
                                break;
                            case 0:
                                end = matcher.end();
                        }
                        ;
                        switch (tableID) {
                            case 0:
                                System.out.print("Найден разделитель ");
                                break;
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                System.out.print("Найдена операция языка ");
                                break;

                            case 5:
                                System.out.print("Найдено правило, определяющее структуру программы ");
                                break;

                            case 6:
                                System.out.print("Найдена переменная ");
                                break;

                            case 7:
                                System.out.print("Найден тип данных ");
                                break;

                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                                System.out.print("Найдено правило, определяющее оператор программы ");
                                break;
                            case 16:
                                System.out.print("Найдено правило, определяющее коментарий программы ");
                                break;
                        }

                        System.out.println(text.substring(start, end) + " с " + start + " по " + (end - 1) + " позицию." + " Код лексемы: " + "(" + tableID + ", " + i + ")");
                        leksems.get(index).add(new Leksema(tableID, i, start, end));
                    }
                    different_symbol = different_symbol.replaceAll(symbChains.get(j).get(i), "");
                }
                tableID++; //Увеличиваем идентификатор таблицы корректных цепочек символов на 1

            count ++;
        }
        ArrayList<String> Dif_symb = getList(different_symbol);
        different_symbols.add(Dif_symb);

        Set<String> set = new HashSet<>(different_symbols.get(index));
        different_symbols.get(index).clear();
        different_symbols.get(index).addAll(set);
        symbChains.add(different_symbols.get(index));


        List<String> symbChain = different_symbols.get(index);
        for (int i=0; i<symbChain.size(); i++) {

                //компилируем паттерн из табличных цепочек символов
                pattern = Pattern.compile("\\b(" + symbChain.get(i) + "\\.|\\b" +
                        symbChain.get(i) + "\\b;|\\b" +
                        symbChain.get(i) + "\\b,|\\b" +
                        symbChain.get(i) + "\\b:|\\b" +
                        symbChain.get(i) + "\\b |\\b" +
                        symbChain.get(i) + "\\b$)");
                //сканируем строку
                Matcher matcher = pattern.matcher(text);
                //В цикле перебираем результат сканирования и выводим информацию о лексемах
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = 0;
                    if (matcher.end() != source.length()) {
                        end = matcher.end() - 1;
                    } else {
                        end = matcher.end();
                    }


                    System.out.println("Найден незазервированный символ " + text.substring(start, end) + " с " + start + " по " + (end - 1) + " позицию." + " Код лексемы: " + "(" + tableID + ", " + i + ")");

                    leksems.get(index).add(new Leksema(tableID+index, i, start, end));
                }
            }

    }

public static String getSorce(List<Leksema> leksems){
//    for (int i = 0; i < leksems.size(); i++) {
//        for (int j = i; j < leksems.size(); j++) {
//            if(leksems.get(i).getBeginId() < leksems.get(j).getBeginId()){
//                Leksema leksema = leksems.get(i);
//
//                leksems.add(i,leksems.get(j));
//                leksems.remove(i);
//
//                leksems.add(j, leksema);
//                leksems.remove(j);
//            }
//        }


   // }

    Collections.sort(leksems, new Comparator<Leksema>() {
        @Override
        public int compare(Leksema o1, Leksema o2) {
            return o1.getBeginId() > o2.getBeginId() ? 1:o1.getBeginId() < o2.getBeginId() ? -1 : 0;
        }
    });

    String result = "";
    for (int i = 0; i < leksems.size(); i ++){
        if(i == leksems.size() - 1){

                result += symbChains.get(leksems.get(i).getTableId()).get(leksems.get(i).getWordId());


        }else if (leksems.get(i+1).getTableId() == 0) {

                result += symbChains.get(leksems.get(i).getTableId()).get(leksems.get(i).getWordId());

        }else {

                result += symbChains.get(leksems.get(i).getTableId()).get(leksems.get(i).getWordId())+" ";

        }
    }

    result = result.replaceAll("\\\\", "");
    result = result.replaceAll("  ", " ");
    return result;
}

    public static String getSorceLeksem(ArrayList<Leksema> leksems){


        Collections.sort(leksems, new Comparator<Leksema>() {
            @Override
            public int compare(Leksema o1, Leksema o2) {
                return o1.getBeginId() > o2.getBeginId() ? 1:o1.getBeginId() < o2.getBeginId() ? -1 : 0;
            }
        });

        String result = "";
        for (int i = 0; i < leksems.size(); i ++){
            if(i == leksems.size() - 1){
                result += "(" + leksems.get(i).getTableId() + "," + leksems.get(i).getWordId() + ")";
            }else if (leksems.get(i+1).getTableId() == 0) {
                result += "(" + leksems.get(i).getTableId() + "," + leksems.get(i).getWordId() + ")";
            }else {
                result += "(" + leksems.get(i).getTableId() + "," + leksems.get(i).getWordId() + ")" + " ";
            }
        }

        result = result.replaceAll("\\\\", "");
        result = result.replaceAll("  ", " ");
        return result;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("------------------ цепочки символов, которые не являются зарезервированными словами,разделителями или ограничителями -----------------------");

            File file = new File("cod.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            int index = 0;
            String line = reader.readLine();

            while (line != null) {
                source.add(line);
                line = reader.readLine();
                System.out.println("------------------ цепочки символов, которые не являются зарезервированными словами,разделителями или ограничителями в строке №"+ index + "-----------------------");

                getLexems(source.get(index),index);
                index++;
            }
            reader.close();

        for(int i = 0; i < source.size();i++) {
            System.out.println();
            System.out.println(getSorceLeksem((ArrayList<Leksema>) leksems.get(i)));
            System.out.println(getSorce(leksems.get(i)));
        }


        Parser parser = new Parser();
        parser.Parsing(source);

    }
}