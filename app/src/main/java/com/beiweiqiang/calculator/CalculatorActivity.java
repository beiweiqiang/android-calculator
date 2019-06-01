package com.beiweiqiang.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {
  private static final String TAG = CalculatorActivity.class.getSimpleName();
  private String string = "";
  TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calculator);

    init();
  }

//  初始化, 绑定
  private void init() {
    Button button = findViewById(R.id.item0);
    button.setOnClickListener(this);
    Button button1 = findViewById(R.id.item1);
    button1.setOnClickListener(this);
    Button button2 = findViewById(R.id.item2);
    button2.setOnClickListener(this);
    Button button3 = findViewById(R.id.item3);
    button3.setOnClickListener(this);
    Button button4 = findViewById(R.id.item4);
    button4.setOnClickListener(this);
    Button button5 = findViewById(R.id.item5);
    button5.setOnClickListener(this);
    Button button6 = findViewById(R.id.item6);
    button6.setOnClickListener(this);
    Button button7 = findViewById(R.id.item7);
    button7.setOnClickListener(this);
    Button button8 = findViewById(R.id.item8);
    button8.setOnClickListener(this);
    Button button9 = findViewById(R.id.item9);
    button9.setOnClickListener(this);
    Button button10 = findViewById(R.id.left_bracket);
    button10.setOnClickListener(this);
    Button button11 = findViewById(R.id.right_bracket);
    button11.setOnClickListener(this);
    Button button12 = findViewById(R.id.add);
    button12.setOnClickListener(this);
    Button button13 = findViewById(R.id.minus);
    button13.setOnClickListener(this);
    Button button14 = findViewById(R.id.mul);
    button14.setOnClickListener(this);
    Button button15 = findViewById(R.id.equal);
    button15.setOnClickListener(this);
    Button button16 = findViewById(R.id.divide);
    button16.setOnClickListener(this);
    textView = findViewById(R.id.cal_txt);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.item0:
        string = string + "0";
        break;
      case R.id.item1:
        string = string + "1";
        break;
      case R.id.item2:
        string = string + "2";
        break;
      case R.id.item3:
        string = string + "3";
        break;
      case R.id.item4:
        string = string + "4";
        break;
      case R.id.item5:
        string = string + "5";
        break;
      case R.id.item6:
        string = string + "6";
        break;
      case R.id.item7:
        string = string + "7";
        break;
      case R.id.item8:
        string = string + "8";
        break;
      case R.id.item9:
        string = string + "9";
        break;
      case R.id.add:
        string = string + "+";
        break;
      case R.id.minus:
        string = string + "-";
        break;
      case R.id.mul:
        string = string + "x";
        break;
      case R.id.divide:
        string = string + "/";
        break;
      case R.id.equal:
        Log.d(TAG, "116 -> " + "onClick: " + string);
        if (string.substring(0, 1).equals("-")) {
          string = calcute("0-" + string.substring(1));
        }
        string = calcute(string);
        break;
      case R.id.left_bracket:
        string = string + "(";
        break;
      case R.id.right_bracket:
        string = string + ")";
        break;
    }

    textView.setText(string);
  }

  // 替换字符串, 范围包括 start, 不包括 end
  private String replaceSubStr(String originStr, int start, int end, String rep) {
    return originStr.substring(0, start) + rep + originStr.substring(end);
  }

  // 检查是否有括号
  private boolean checkBracket(String str) {
    Pattern p = Pattern.compile("[()]");
    Matcher m = p.matcher(str);
    boolean b = m.find();
    return b;
  }

  private String calcute(String str) {
    if (!checkBracket(str)) {
      return calcuteWithoutBracket(str);
    }

    String originStr = str;
    Stack<int[]> bracketStack = new Stack<>();
    int startIndex = 0, endIndex = 0;

    for (int i = 0; i < originStr.length(); i++) {
      char c = originStr.charAt(i);

      // '(' 放 0, ')' 放 1
      // stack 内每个元素是 int[], index0 放 0 / 1 表示括号, index1 放括号在字符串中的索引位置
      if (c == '(') {
        bracketStack.push(new int[]{0, i});
      }
      if (c == ')') {
        if (bracketStack.peek()[0] == 0) {
          startIndex = bracketStack.peek()[1];
          endIndex = i + 1;
          break;
        }
        bracketStack.push(new int[]{1, i});
      }
    }

    originStr = replaceSubStr(originStr, startIndex, endIndex, calcuteWithoutBracket(originStr.substring(startIndex + 1, endIndex - 1)));

    Pattern p = Pattern.compile("[()]");
    Matcher m = p.matcher(originStr);
    boolean b = m.find();
    if (b) {
      return calcute(originStr);
    }

    return calcuteWithoutBracket(originStr);
  }

  // 计算 没有括号 的算术字符串
  private String calcuteWithoutBracket(String str) {
    String originStr = str;

    Queue<String> exps = new LinkedList<>(Arrays.asList(originStr.split("[+\\-]")));
    Queue<String> handledExps = new LinkedList<>();
    Queue<String> syms = new LinkedList<>();

    Pattern pattern = Pattern.compile("[+\\-]");
    Matcher matcher = pattern.matcher(str);
    while (matcher.find()) {
      syms.offer(matcher.group());
    }
    while (exps.peek() != null) {
      handledExps.offer(calcuteInOrder(exps.poll()));
    }

    String handledStr = handledExps.poll();
    while (syms.peek() != null) {
      handledStr = handledStr + syms.poll() + handledExps.poll();
    }

    return calcuteInOrder(handledStr);
  }

  // 执行按顺序计算的字符串, 比如: 3+2-1, 3x1/11 ... , 即字符串中只存在相同优先级的 symbol
  private String calcuteInOrder(String str) {
    if (!checkSymbol(str)) {
      // 如果传入的字符串中没有 symbol, 则直接返回该字符串
      return str;
    }

    String originStr = str;
    String[] number = originStr.split("[+\\-x/]");
    ArrayList<String> symbol = new ArrayList<>();
    Pattern pattern = Pattern.compile("[+\\-x/]");
    Matcher matcher = pattern.matcher(str);
    // Check all occurrences
    while (matcher.find()) {
      // matcher.start() 是字符的位置索引, matcher.group() 是字符
      symbol.add(matcher.group());
    }
    Queue<String> numberQueue = new LinkedList<>(Arrays.asList(number));
    Queue<String> symbolQueue = new LinkedList<>(symbol);

    String op1, op2, sym;
    op1 = numberQueue.poll();
    while (numberQueue.peek() != null || symbolQueue.peek() != null) {
      sym = symbolQueue.poll();
      op2 = numberQueue.poll();
      double num1 = strToNumber(op1);
      double num2 = strToNumber(op2);
      switch (sym) {
        case "+":
          op1 = numberToStr(num1 + num2);
          break;
        case "-":
          op1 = numberToStr(num1 - num2);
          break;
        case "x":
          op1 = numberToStr(num1 * num2);
          break;
        case "/":
          op1 = numberToStr(num1 / num2);
          break;
      }
    }

    return op1;
  }

  // 将字符串转换为 double
  private double strToNumber(String str) {
    return Double.parseDouble(str);
  }
  // 将 double 转换为字符串
  private String numberToStr(double num) {
    double d = num;
    if (d == (int) d) {
      return Integer.toString((int) d);
    }
    return Double.toString(d);
  }

  // 判断传入字符串中, 是否有符号
  // 字符串中有符号返回 true
  // 字符串中无符号返回 false
  private boolean checkSymbol(String str) {
    Pattern p = Pattern.compile("[+\\-x/()]");
    Matcher m = p.matcher(str);
    boolean b = m.find();
    return b;
  }
}
