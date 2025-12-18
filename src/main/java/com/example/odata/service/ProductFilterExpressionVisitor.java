package com.example.odata.service;

import com.example.odata.model.Product;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;

import java.util.List;
import java.util.Locale;

public class ProductFilterExpressionVisitor implements ExpressionVisitor<Object> {

    private final Product currentProduct;

    public ProductFilterExpressionVisitor(Product product) {
        this.currentProduct = product;
    }

    @Override
    public Object visitBinaryOperator(BinaryOperatorKind operator, Object left, List<Object> right)
            throws ODataApplicationException {
        // Logic for List based operator (like IN) or delegate to single object version
        if (right != null && !right.isEmpty()) {
            return visitBinaryOperator(operator, left, right.get(0));
        }
        throw new ODataApplicationException("Invalid binary operation", HttpStatusCode.BAD_REQUEST.getStatusCode(),
                Locale.ENGLISH);
    }

    @Override
    public Object visitBinaryOperator(BinaryOperatorKind operator, Object left, Object right)
            throws ODataApplicationException {
        // Binary Operators like eq, ne, gt, ge, lt, le, and, or
        if (operator == BinaryOperatorKind.AND || operator == BinaryOperatorKind.OR) {
            return evaluateBooleanOperation(operator, left, right);
        } else {
            return evaluateComparisonOperation(operator, left, right);
        }
    }

    @Override
    public Object visitUnaryOperator(UnaryOperatorKind operator, Object operand) throws ODataApplicationException {
        if (operator == UnaryOperatorKind.NOT && operand instanceof Boolean) {
            return !(Boolean) operand;
        } else if (operator == UnaryOperatorKind.MINUS && operand instanceof Number) {
            // Simple negation for numbers
            if (operand instanceof Integer)
                return -(Integer) operand;
            if (operand instanceof Double)
                return -(Double) operand;
        }
        throw new ODataApplicationException("Invalid type for unary operator",
                HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
    }

    @Override
    public Object visitMethodCall(MethodKind methodCall, List<Object> parameters) throws ODataApplicationException {
        if (methodCall == MethodKind.CONTAINS) {
            if (parameters.get(0) instanceof String && parameters.get(1) instanceof String) {
                return ((String) parameters.get(0)).contains((String) parameters.get(1));
            }
        }
        if (methodCall == MethodKind.STARTSWITH) {
            if (parameters.get(0) instanceof String && parameters.get(1) instanceof String) {
                return ((String) parameters.get(0)).startsWith((String) parameters.get(1));
            }
        }
        // Add more methods like tolower, toupper etc if needed.
        return false;
    }

    @Override
    public Object visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression) {
        return null; // Not implemented for this simple example
    }

    @Override
    public Object visitLiteral(Literal literal) {
        // Olingo passes literals as strings (e.g. "'Bike'", "10", "15.5")
        // We need to try parsing them.
        String literalText = literal.getText();
        if (literalText.startsWith("'") && literalText.endsWith("'")) {
            return literalText.substring(1, literalText.length() - 1); // String
        }
        try {
            return Integer.parseInt(literalText);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(literalText);
            } catch (NumberFormatException e2) {
                // Boolean or other
                if ("true".equalsIgnoreCase(literalText))
                    return true;
                if ("false".equalsIgnoreCase(literalText))
                    return false;
            }
        }
        return literalText;
    }

    @Override
    public Object visitMember(Member member) throws ODataApplicationException {
        // Retrieves the value of the property from the current product
        UriResource uriResource = member.getResourcePath().getUriResourceParts().get(0);

        if (uriResource instanceof UriResourcePrimitiveProperty) {
            String propertyName = ((UriResourcePrimitiveProperty) uriResource).getProperty().getName();
            if ("ID".equals(propertyName)) {
                return currentProduct.getId();
            } else if ("Name".equals(propertyName)) {
                return currentProduct.getName();
            } else if ("Description".equals(propertyName)) {
                return currentProduct.getDescription();
            } else if ("Price".equals(propertyName)) {
                return currentProduct.getPrice();
            }
        }
        return null;
    }

    @Override
    public Object visitAlias(String aliasName) {
        return null;
    }

    @Override
    public Object visitTypeLiteral(EdmType type) {
        return null;
    }

    @Override
    public Object visitLambdaReference(String variableName) {
        return null;
    }

    @Override
    public Object visitEnum(EdmEnumType type, List<String> enumValues) {
        return null;
    }

    // --- Helper Methods ---

    private Object evaluateBooleanOperation(BinaryOperatorKind operator, Object left, Object right)
            throws ODataApplicationException {
        if (left instanceof Boolean && right instanceof Boolean) {
            if (operator == BinaryOperatorKind.AND)
                return (Boolean) left && (Boolean) right;
            if (operator == BinaryOperatorKind.OR)
                return (Boolean) left || (Boolean) right;
        }
        throw new ODataApplicationException("Boolean operation expects boolean operands",
                HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
    }

    private Object evaluateComparisonOperation(BinaryOperatorKind operator, Object left, Object right)
            throws ODataApplicationException {
        if (left instanceof Number && right instanceof Number) {
            double l = ((Number) left).doubleValue();
            double r = ((Number) right).doubleValue();
            if (operator == BinaryOperatorKind.EQ)
                return l == r;
            if (operator == BinaryOperatorKind.NE)
                return l != r;
            if (operator == BinaryOperatorKind.GT)
                return l > r;
            if (operator == BinaryOperatorKind.GE)
                return l >= r;
            if (operator == BinaryOperatorKind.LT)
                return l < r;
            if (operator == BinaryOperatorKind.LE)
                return l <= r;
        } else if (left instanceof String && right instanceof String) {
            String l = (String) left;
            String r = (String) right;
            if (operator == BinaryOperatorKind.EQ)
                return l.equals(r);
            if (operator == BinaryOperatorKind.NE)
                return !l.equals(r);
            // String comparisons (alphabetical) could be added here
        }

        // Use default equals for other types
        if (operator == BinaryOperatorKind.EQ)
            return left.equals(right);
        if (operator == BinaryOperatorKind.NE)
            return !left.equals(right);

        return false;
    }
}
