package com.back;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Calc {
    public static int run(String cmd) {
        String[] bits = cmd.trim().split(" ");
        List<String> tokens = Arrays.stream(bits).collect(Collectors.toList());

        //괄호처리
        tokens = bracketOpr(tokens);

        //곱셈 우선 계산
        int result = CalOpr(tokens);
        return result;
    }

    private static int CalOpr(List<String> tokens) {
        for(int i = 0; i < tokens.size(); i++){   //*가 i번째니까 i-1, i, i+1을 하나의 값으로
            String op = tokens.get(i);
            if ("*".equals(op) || "/".equals(op)) {
                int a = Integer.parseInt(tokens.get(i - 1));
                int b = Integer.parseInt(tokens.get(i + 1));
                int result = "*".equals(op) ? a * b : a / b;

                tokens.set(i - 1, String.valueOf(result));
                tokens.remove(i);     // 연산자 제거
                tokens.remove(i);     // 오른쪽 피연산자 제거
                i--;                  // 인덱스 보정
            }
        }

        int result = Integer.parseInt(tokens.get(0));

        result += IntStream.range(1, tokens.size())
                .filter(i -> i % 2 == 1) // 연산자 위치
                .map(i -> {
                    int n = Integer.parseInt(tokens.get(i + 1));
                    return tokens.get(i).equals("+") ? n : -n;
                })
                .sum();
        return result;
    }

    private static List<String> bracketOpr(List<String> tokens) {
        int start = -1;
        int end = -1;
        boolean minus = false;

        //제일 안쪽 괄호 찾기(-여부 확인)
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).contains("(")) {
                start = i;
                minus = tokens.get(i).startsWith("-");
            }
            if (tokens.get(i).contains(")")) {
                end = i;
                break;
            }
        }

        // 더 이상 괄호가 없으면 그대로 반환 (재귀 종료)
        if (start == -1 || end == -1) return tokens;

        // 괄호 개수 구하기
        int startCount = tokens.get(start)
                .replace("-(", "(")
                .replaceAll("^([(]+).*", "$1")
                .length();
        int endCount = tokens.get(end)
                .replaceAll(".*?([)]+)$", "$1")
                .length();

        //괄호안만 모으기
        List<String> inner = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            String s = tokens.get(i)
                        .replace("-(", "")
                        .replace("(", "") // 괄호 제거
                        .replace(")", "");
            inner.add(s);
        }

        // 괄호 안 계산
        int val = CalOpr(inner);
        if (minus) val = -val;

        // 괄호 개수 보정
        String a = switch (Integer.compare(startCount, endCount)) {
            case 1 -> "(".repeat(startCount - endCount) + val;
            case 0 -> String.valueOf(val);
            default -> val + ")".repeat(endCount - startCount);
        };

        // 기존 괄호 부분 제거
        for (int i = end; i >= start; i--) tokens.remove(i);
        tokens.add(start, a);

        return bracketOpr(tokens);
    }

}