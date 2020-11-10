package com.facilio.db.criteria.manager;

import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.util.ExpressionEvaluator;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.Predicate;

import java.util.*;

public class NamedCriteria extends ExpressionEvaluator<Predicate> {

    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long orgId = -1L;
    public long getOrgId() {
        return orgId;
    }
    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private long namedCriteriaModuleId = -1;
    public long getNamedCriteriaModuleId() {
        return namedCriteriaModuleId;
    }
    public void setNamedCriteriaModuleId(long namedCriteriaModuleId) {
        this.namedCriteriaModuleId = namedCriteriaModuleId;
    }

    private String pattern;
    public String getPattern() {
        return pattern;
    }
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    private Map<String, NamedCondition> conditions = null;
    public Map<String, NamedCondition> getConditions() {
        return conditions;
    }
    public void setConditions(Map<String, NamedCondition> conditions) {
        this.conditions = conditions;
    }
    public void addCondition(String key, NamedCondition condition) {
        if (conditions == null) {
            conditions = new HashMap<>();
        }
        conditions.put(key, condition);
    }

    public boolean evaluate(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        Predicate predicate = evaluateExpression(pattern);
//        predicate.

        return false;
    }

    @Override
    public Predicate getOperand(String s) {
        return new BooleanOperators.TruePredicate();
    }

    @Override
    public Predicate applyOp(String operator, Predicate leftOperand, Predicate rightOperand) {
        if (operator.equalsIgnoreCase("and")) {
            return PredicateUtils.andPredicate(leftOperand, rightOperand);
        } else {
            return operator.equalsIgnoreCase("or") ? PredicateUtils.orPredicate(leftOperand, rightOperand) : null;
        }
    }

    public void validate() {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be empty for named criteria");
        }

        if (MapUtils.isEmpty(conditions)) {
            throw new IllegalArgumentException("Conditions cannot be empty");
        }
    }

    public void validatePattern() {
        String[] tokens = this.tokenize();
        this.postfix(tokens);
    }

    private String[] tokenize() {
        String pattern = "(" + this.pattern + ")";
        List<String> tokens = new ArrayList();
        String curr = "";

        for(int i = 0; i < pattern.length(); ++i) {
            String c = Character.toString(pattern.charAt(i));
            if (c.equals("(")) {
                tokens.add(c);
            } else if (c.equals(")")) {
                if (!curr.isEmpty()) {
                    tokens.add(curr);
                    curr = "";
                }

                tokens.add(c);
            } else if (this.isNumber(c)) {
                if (!curr.equalsIgnoreCase("and") && !curr.equalsIgnoreCase("or") && !curr.equalsIgnoreCase("(")) {
                    if (curr.isEmpty()) {
                        curr = curr + c;
                    } else if (this.isNumber(curr.charAt(curr.length() - 1))) {
                        curr = curr + c;
                    } else {
                        tokens.add(curr);
                        curr = c;
                    }
                } else {
                    tokens.add(curr);
                    curr = "";
                }
            } else if (this.isAlphabet(c)) {
                if (!curr.isEmpty() && this.isNumber(curr.charAt(curr.length() - 1))) {
                    tokens.add(curr);
                    curr = "";
                }

                curr = curr + c;
                if (curr.equalsIgnoreCase("and") || curr.equalsIgnoreCase("or")) {
                    tokens.add(curr);
                    curr = "";
                }
            } else if (this.isSpace(c)) {
                if (!curr.isEmpty()) {
                    tokens.add(curr);
                    curr = "";
                }
            } else {
                if (!curr.isEmpty()) {
                    tokens.add(curr);
                }

                curr = "";
                tokens.add(c);
            }
        }

        if (!curr.isEmpty()) {
            tokens.add(curr);
        }

        return (String[])tokens.toArray(new String[tokens.size()]);
    }

    public String postfix(String[] tokens) {
        Stack<String> stack = new Stack();
        List<String> outPut = new ArrayList();
        String prev = null;
        String[] var5 = tokens;
        int var6 = tokens.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String e = var5[var7];
            boolean pass = this.isNumber(e) || e.equalsIgnoreCase("and") || e.equalsIgnoreCase("or") || e.equals(")") || e.equals("(");
            if (!pass) {
                throw new IllegalArgumentException("Invalid Character");
            }

            if (e.equals("(") && prev != null && !prev.equals("(") && !prev.equalsIgnoreCase("and") && prev.equalsIgnoreCase("or")) {
                throw new IllegalArgumentException("Invalid Expression");
            }

            if (e.equals(")") && !prev.equals(")") && !this.isNumber(prev)) {
                throw new IllegalArgumentException("Invalid Expression");
            }

            if ((e.equalsIgnoreCase("and") || e.equalsIgnoreCase("or")) && !prev.equals(")") && !this.isNumber(prev)) {
                throw new IllegalArgumentException("Invalid Expression");
            }

            if (this.isNumber(e) && prev != null && !prev.equals("(") && !prev.equalsIgnoreCase("and") && !prev.equalsIgnoreCase("or")) {
                throw new IllegalArgumentException("Invalid Expression");
            }

            if (e.equals("(")) {
                stack.push(e);
            } else if (this.isNumber(e)) {
                outPut.add(e);
            } else {
                String p;
                if (e.equalsIgnoreCase("and")) {
                    while(!stack.isEmpty()) {
                        p = (String)stack.peek();
                        if (p.equals("(") || p.equalsIgnoreCase("or")) {
                            break;
                        }

                        outPut.add(stack.pop());
                    }

                    stack.add(e);
                } else if (!e.equalsIgnoreCase("or")) {
                    if (e.equals(")")) {
                        while(!stack.isEmpty()) {
                            p = (String)stack.pop();
                            if (p.equals("(")) {
                                break;
                            }

                            outPut.add(p);
                        }
                    }
                } else {
                    while(!stack.isEmpty()) {
                        p = (String)stack.peek();
                        if (p.equals("(")) {
                            break;
                        }

                        outPut.add(stack.pop());
                    }

                    stack.add(e);
                }
            }

            prev = e;
        }

        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("Invalid Expression");
        } else {
            return StringUtils.join(outPut.toArray(new String[outPut.size()]));
        }
    }

    private boolean isSpace(String c) {
        return StringUtils.isBlank(c);
    }

    private boolean isAlphabet(String c) {
        return StringUtils.isAlpha(c);
    }

    private boolean isNumber(char c) {
        return this.isNumber(Character.toString(c));
    }

    private boolean isNumber(String c) {
        return StringUtils.isNumeric(c);
    }
}
