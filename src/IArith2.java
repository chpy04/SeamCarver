import tester.*;
import java.util.function.*;

// class to represent a negating function
class Neg implements Function<Double, Double> {
  // multiplies input by -1 to negate
  public Double apply(Double input) {
    return -1 * input;
  }
}

// class to represent a squaring function
class Sqr implements Function<Double, Double> {
  // squares input
  public Double apply(Double input) {
    return Math.pow(input, 2);
  }
}

// class to represent an adding function
class Plus implements BiFunction<Double, Double, Double> {
  // adds inputs
  public Double apply(Double input, Double input2) {
    return input + input2;
  }
}

// class to represent a minus function
class Minus implements BiFunction<Double, Double, Double> {
  // subtracts inputs
  public Double apply(Double input, Double input2) {
    return input - input2;
  }
}

// class to represent a multiply function
class Multiply implements BiFunction<Double, Double, Double> {
  // multiplies inputs
  public Double apply(Double input, Double input2) {
    return input * input2;
  }
}

// class to represent a dividing function
class Divide implements BiFunction<Double, Double, Double> {
  // divides inputs
  public Double apply(Double input, Double input2) {
    return input / input2;
  }
}

// An IArithVisitor is a function over IAriths
interface IArithVisitor<R> extends Function<IArith<R>, R> {
  R visitConst(Const<R> c); // function for Consts
  R visitUnaryFormula(UnaryFormula<R> u); // function for Unary Formulas
  R visitBinaryFormula(BinaryFormula<R> b); // function for Binary Formulas
}

// An EvalVisitor is a function to evaluate the value of an IArith
class EvalVisitor implements IArithVisitor<Double> {
  
  // returns the value of a constant
  public Double visitConst(Const<Double> c) {
    return c.num;
  }
  
  // returns the value of the function applied to the evaluated child
  public Double visitUnaryFormula(UnaryFormula<Double> u) {
    return u.func.apply(this.apply(u.child));
  }
  
  //returns the value of the function applied to the evaluated left 
  // and evaluated right
  public Double visitBinaryFormula(BinaryFormula<Double> b) {
    return b.func.apply(this.apply(b.left), this.apply(b.right));
  }
  
  // applies evaluate function to given IArith
  public Double apply(IArith<Double> i) {
    return i.accept(this);
  }
}

// A PrintVisitor is a function to print the IArith
class PrintVisitor implements IArithVisitor<String> {
  
  // returns a string of a constant
  public String visitConst(Const<String> c) {
    return c.num.toString();
  }
  
  public String visitUnaryFormula(UnaryFormula<String> u) {
    return "(" + u.name + " " + this.apply(u.child) + ")";
  }
  
  public String visitBinaryFormula(BinaryFormula<String> b) {
    return "(" + b.name + " " + this.apply(b.left) + " " + 
      this.apply(b.right) + ")";
  }
  
  public String apply(IArith<String> i) {
    return i.accept(this);
  }
}

class DoublerVisitor<R> implements IArithVisitor<IArith<R>> {
  public IArith<R> visitConst(Const<IArith<R>> c) {
    return new Const<R>(c.num * 2);
  }
  
  public IArith<R> visitUnaryFormula(UnaryFormula<IArith<R>> u) {
    return new UnaryFormula<R>(u.func, u.name, this.apply(u.child));
  }
  
  public IArith<R> visitBinaryFormula(BinaryFormula<IArith<R>> b) {
    return new BinaryFormula<R>(b.func, b.name, this.apply(b.left), this.apply(b.right));
  }
  
  public IArith<R> apply(IArith<R> i) {
    return i.accept(this);
  }
}

class AllSmallVisitor implements IArithVisitor<Boolean> {
  public Boolean visitConst(Const c) {
    return c.num < 10;
  }
  
  public Boolean visitUnaryFormula(UnaryFormula u) {
    return this.apply(u.child);
  }
  
  public Boolean visitBinaryFormula(BinaryFormula b) {
    return this.apply(b.left) && this.apply(b.right);
  }
  
  public Boolean apply(IArith<Boolean> i) {
    return i.accept(this);
  }
}

class NoDivBy0 implements IArithVisitor<Boolean> {
  public Boolean visitConst(Const c) {
    return true;
  }
  
  public Boolean visitUnaryFormula(UnaryFormula u) {
    return this.apply(u.child);
  }
  
  public Boolean visitBinaryFormula(BinaryFormula b) {
    return (b.name.equals("div") && 
        (!(new EvalVisitor().apply(b.right) >= -0.0001 && 
        new EvalVisitor().apply(b.right) <= 0.0001)) &&
        this.apply(b.left) && this.apply(b.right));
  }
  
  public Boolean apply(IArith<Boolean> i) {
    return i.accept(this);
  }
}

class NoNegativeResults implements IArithVisitor<Boolean> {
  public Boolean visitConst(Const c) {
    return c.num > 0;
  }
  
  public Boolean visitUnaryFormula(UnaryFormula u) {
    return this.apply(u.child);
  }
  
  public Boolean visitBinaryFormula(BinaryFormula b) {
    return this.apply(b.left) && this.apply(b.right);
  }
  
  public Boolean apply(IArith<Boolean> i) {
    return i.accept(this);
  }
}

// interface for arithmetic 
interface IArith<R> {
  
  // method to accept visitor
  R accept(IArithVisitor<R> i);

}

// class to represent a constant value
class Const<R> implements IArith<R> {
  Double num;
  
  // constructor
  Const(Double num) {
    this.num = num;  
    }
  
  // accepts visitor and calls visitConst on this
  public R accept(IArithVisitor<R> i) {
    return i.visitConst(this);
  }

}

//class to represent an unary formula
class UnaryFormula<R> implements IArith<R> {
  Function<Double, Double> func;
  String name;
  IArith<R> child;

  // constructor
  UnaryFormula(Function<Double, Double> func, String name, IArith<R> child) {
    this.func = func;  
    this.name = name;
    this.child = child;
  }
  
  // accepts visitor and calls visitUnaryFormula on this
  public R accept(IArithVisitor<R> i) {
    return i.visitUnaryFormula(this);
  }

}

//class to represent a binary formula
class BinaryFormula<R> implements IArith<R> {
  BiFunction<Double, Double, Double> func;
  String name;
  IArith<R> left;
  IArith<R> right;

  // constructor
  BinaryFormula(BiFunction<Double, Double, Double> func, String name, 
      IArith<R> left, IArith<R> right) {
    this.func = func;  
    this.name = name;
    this.left = left;
    this.right = right;
  }
  
  // accepts visitor and calls visitBinaryFormula on this
  public R accept(IArithVisitor<R> i) {
    return i.visitBinaryFormula(this);
  }
}

class ExamplesFormulas {
  IArith a = new Const(5.0);
}

