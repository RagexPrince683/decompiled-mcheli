package mcheli.eval.eval.rule;

import mcheli.eval.eval.exp.AbstractExpression;
import mcheli.eval.eval.exp.Col2Expression;
import mcheli.eval.eval.lex.Lex;

public class Col2Rule extends AbstractRule {
	public Col2Rule(ShareRuleValue share) {
		super(share);
	}

	protected AbstractExpression parse(Lex lex) {
		AbstractExpression x = this.nextRule.parse(lex);
		boolean b = false;
		for (;;) {
			switch (lex.getType()) {
			case 2147483634:
				String ope = lex.getOperator();
				if (!isMyOperator(ope))
					b = true;
				int pos = lex.getPos();
				AbstractExpression y = this.nextRule.parse(lex.next());
				x = Col2Expression.create(newExpression(ope, lex.getShare()), lex.getString(), pos, x, y);
			}
			if (b)
				break;
		}
		return x;
	}
}
