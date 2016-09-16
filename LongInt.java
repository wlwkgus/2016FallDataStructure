// LongInt ADT for unbounded integers

import java.math.*;

public class LongInt {
  private char[] invertedIntArray;
  private int length, offset = 0;
  private static int asciiOffset = 48;
  private boolean isPlus = true;
  // constructor
  public LongInt(String s) {
    int index = 0;
    this.length = s.length();
    isPlus = true;

    if(s.charAt(0) == '-'){
      isPlus = false;
      offset = 1;
      this.length--;
      invertedIntArray = new char[s.length()-1];
    } else invertedIntArray = new char[s.length()];

    for(int i=s.length() - 1;i>=offset;i--){
      invertedIntArray[index] = s.charAt(i);
      index++;
    }
  }

  private boolean isPlus(){
    return this.isPlus;
  }
  private void setIsPlus(boolean value) { this.isPlus = value; }

  private int getDigitByIndex(int index){
    try {
      return (int) this.invertedIntArray[index] - asciiOffset;
    } catch (ArrayIndexOutOfBoundsException e) {
      return 0;
    }
  }

  private int isAbsBiggerThan(LongInt opnd){
    // if 1, this > opnd, if -1, this < opnd, if 0, this = opnd
    if(this.getLength() > opnd.getLength()) return 1;
    else if(this.getLength() < opnd.getLength()) return -1;
    else{
      for(int i=0;i<this.getLength();i++){
        int temp1, temp2;
        temp1 = this.getDigitByIndex(this.getLength() - i - 1);
        temp2 = opnd.getDigitByIndex(this.getLength() - i - 1);
        if(temp1 > temp2) return 1;
        else if(temp1 < temp2) return -1;
      }
      return 0;
    }
  }

  private LongInt childMultiply(int digit, int tens){
    int maxLength = this.getLength() + 1, upperAdd = 0;
    StringBuffer strBuf = new StringBuffer();

    for(int i=0;i<maxLength;i++){
      int temp = this.getDigitByIndex(i), result;
      result = temp * digit + upperAdd;
      upperAdd = result / 10;
      result = result % 10;
      strBuf.insert(0, result);
    }

    try {
      while (strBuf.charAt(0) == '0') {
        strBuf.delete(0, 1);
      }
    } catch(StringIndexOutOfBoundsException e){
      return new LongInt("0");
    }

    for(int i=0;i<tens;i++){
      strBuf.append("0");
    }

    return new LongInt(strBuf.toString());
  }

  private String childSubtract(LongInt opnd){
    // Always this > opnd. Return as String type.
    int maxLength = Math.max(this.getLength(), opnd.getLength()), upperMinus = 0;
    StringBuffer strBuf = new StringBuffer();

    for(int i=0;i<maxLength;i++){
      int temp1, temp2, result;
      temp1 = this.getDigitByIndex(i);
      temp2 = opnd.getDigitByIndex(i);
      result = temp1 - temp2 - upperMinus;
      upperMinus = 0;

      if(result < 0){
        upperMinus = 1;
        result += 10;
      }

      strBuf.insert(0, result);
    }

    while(strBuf.charAt(0) == '0'){
      strBuf.delete(0, 1);
    }

    return strBuf.toString();
  }

  // returns 'this' + 'opnd'; Both inputs remain intact.
  public LongInt add(LongInt opnd) {
    int maxLength = Math.max(this.getLength(), opnd.getLength()), upperAdd = 0;
    StringBuffer strBuf = new StringBuffer();
    LongInt finalResult;

    if(!this.isPlus() && opnd.isPlus()){
      this.setIsPlus(true);
      finalResult = opnd.subtract(this);
      this.setIsPlus(false);
      return finalResult;
    }
    if(this.isPlus() && !opnd.isPlus()){
      opnd.setIsPlus(true);
      finalResult = this.subtract(opnd);
      opnd.setIsPlus(false);
      return finalResult;
    }

    for(int i=0;i<maxLength+1;i++){
      int temp1, temp2, result;
      temp1 = this.getDigitByIndex(i);
      temp2 = opnd.getDigitByIndex(i);
      result = temp1 + temp2 + upperAdd;
      upperAdd = 0;

      if(result > 9){
        upperAdd = 1;
        result -= 10;
      }

      strBuf.insert(0, result);
    }



    try {
      while (strBuf.charAt(0) == '0') {
        strBuf.delete(0, 1);
      }
    } catch(StringIndexOutOfBoundsException e){
      return new LongInt("0");
    }

    if(this.isPlus()) return new LongInt(strBuf.toString());
    else{
      strBuf.insert(0, '-');
      return new LongInt(strBuf.toString());
    }
  }

  // returns 'this' - 'opnd'; Both inputs remain intact.
  public LongInt subtract(LongInt opnd) {
    LongInt result;
    if(!this.isPlus() && opnd.isPlus()){
      opnd.setIsPlus(false);
      result = opnd.add(this);
      opnd.setIsPlus(true);
      return result;
    }
    if(this.isPlus() && !opnd.isPlus()){
      opnd.setIsPlus(true);
      result = this.add(opnd);
      opnd.setIsPlus(false);
      return result;
    }

    switch(this.isAbsBiggerThan(opnd)) {
      case 1:
        if(this.isPlus()) return new LongInt(this.childSubtract(opnd));
        else return new LongInt("-" + this.childSubtract(opnd));
      case 0:
        return new LongInt("0");
      case -1:
        if(this.isPlus()) return new LongInt("-" + opnd.childSubtract(this));
        else return new LongInt(opnd.childSubtract(this));
      default:
        return new LongInt("0");
    }
  }

  // returns 'this' * 'opnd'; Both inputs remain intact.
  public LongInt multiply(LongInt opnd) {

    LongInt temp, result = new LongInt("0");
    for(int i=0; i<opnd.getLength(); i++){
      temp = this.childMultiply(opnd.getDigitByIndex(i), i);
      result = result.add(temp);
    }

    if(!this.isPlus() && opnd.isPlus() || this.isPlus() && !opnd.isPlus()) result.setIsPlus(false);
    if(this.isPlus() && opnd.isPlus() || !this.isPlus() && !opnd.isPlus()) result.setIsPlus(true);

    return result;

  }

  // print the value of 'this' element to the standard output.
  public void print() {
    StringBuffer strBuf = new StringBuffer();
    if(!this.isPlus()) strBuf.append('-');

    for(int i=0; i<this.length; i++){
      strBuf.append(this.invertedIntArray[this.length - i - 1]);
    }
    System.out.print(strBuf.toString());
  }

  private int getLength(){
    return this.length;
  }

}

