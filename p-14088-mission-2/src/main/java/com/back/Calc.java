package com.back;

import java.util.ArrayList;
import java.util.List;

public class Calc {
    public static int run(String cmd) {
        String[] bits = cmd.trim().split(" ");
        List<String> tokens = new ArrayList<>();
        for (String b : bits) {
            tokens.add(b);
        }
        System.out.println("처음 시작 " + tokens);

        //괄호처리
        tokens = bracketOpr(tokens);

        //곱셈 우선 계산
        int result = CalOpr(tokens);
        return result;
    }

    private static int CalOpr(List<String> tokens) {
        for(int i = 0; i < tokens.size(); i++){   //*가 i번째니까 i-1, i, i+1을 하나의 값으로
            if(tokens.get(i).equals("*")){
                int mul = Integer.parseInt(tokens.get(i-1)) * Integer.parseInt(tokens.get(i+1));
                tokens.set(i-1, String.valueOf(mul));
                tokens.remove(i);
                tokens.remove(i);
                i--;
            }
        }
        System.out.println("곱셈 계산 후   ) " + tokens);

        int result = Integer.parseInt(tokens.get(0));


        for (int i = 1; i < tokens.size(); i+= 2) {
            String op = tokens.get(i);
            int n =  Integer.parseInt(tokens.get(i+1));
            if(op.equals("+")) result += n;
            if(op.equals("-")) result -= n;
        }
        System.out.println("덧뺄셈 계산 후 ) " + result);
        return result;
    }

    private static List<String> bracketOpr(List<String> tokens) {
        int start = -1;
        int end = -1;
        boolean minus = false;

        //제일 안쪽 괄호 찾기
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).contains("(")) {  //여러개면 제일 마지막꺼
                start = i;
                if (tokens.get(i).startsWith("-")) minus = true;  //괄호 앞에 -가 붙으면 전체 -
            }
            if (tokens.get(i).contains(")")) {           //여러개여도 제일 처음꺼
                end = i;
                break;                                   //그러면 제일 안쪽 괄호를 찾을 수 있다
            }
        }

        // 더 이상 괄호가 없으면 그대로 반환 (재귀 종료)
        if (start == -1 || end == -1) return tokens;

        // 괄호 개수 구하기
        int start_count = tokens.get(start)
                .replace("-(", "(")
                .replaceAll("^([(]+).*", "$1")
                .length();
        int end_count = tokens.get(end)
                .replaceAll(".*?([)]+)$", "$1")
                .length();

        System.out.println("괄호           ) " + tokens);
        System.out.println("                 "+start_count + "앞괄호" + end_count + "뒷괄호");

        List<String> inner = new ArrayList<>();
        for (int i = start; i <= end; i++) {
                //replace(a, b) 이면 문자열 a가 b로 전환됨 아래는 괄호를 없애는 용도
            String s = tokens.get(i)
                        .replace("-(", "")
                        .replace("(", "") // 괄호 제거
                        .replace(")", "");
            inner.add(s);
        }
        System.out.println("inner          ) " + inner);

        // 괄호 안 계산
        int val = CalOpr(inner);
        if (minus) val = -val;

        // 괄호 개수 보정
        String a = "";
        if (start_count > end_count) a = "(".repeat(start_count - 1) + val;
        else if (start_count == end_count) a = String.valueOf(val);
        else if (start_count < end_count) a = val + ")".repeat(end_count - 1);

        // 기존 괄호 부분 제거
        for (int i = end; i >= start; i--) tokens.remove(i);
        tokens.add(start, a);

        return bracketOpr(tokens);
    }

}