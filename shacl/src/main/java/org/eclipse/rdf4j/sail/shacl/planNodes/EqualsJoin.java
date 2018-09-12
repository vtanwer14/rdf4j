package org.eclipse.rdf4j.sail.shacl.planNodes;

import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.SailException;

public class EqualsJoin implements PlanNode{
	PlanNode left;
	PlanNode right;

	public EqualsJoin(PlanNode left, PlanNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public CloseableIteration<Tuple, SailException> iterator() {
		return new CloseableIteration<Tuple, SailException>() {

			CloseableIteration<Tuple, SailException> leftIterator = left.iterator();
			CloseableIteration<Tuple, SailException> rightIterator = right.iterator();

			Tuple next;
			Tuple nextLeft;
			Tuple nextRight;

			void calculateNext() {
				if (next != null) {
					return;
				}

				if (nextLeft == null && leftIterator.hasNext()) {
					nextLeft = leftIterator.next();
				}


				if (nextRight == null && rightIterator.hasNext()) {
					nextRight = rightIterator.next();
				}

				if (nextLeft == null) {
//					if (discardedRight != null) {
//						while(nextRight != null){
//							discardedRight.push(nextRight);
//							if(rightIterator.hasNext()){
//								nextRight = rightIterator.next();
//							}else{
//								nextRight = null;
//							}
//						}
//					}
					return;
				}


				while (next == null) {
					if (nextRight != null) {

						if (nextLeft.line == nextRight.line || nextLeft.line.equals(nextRight.line)) {
							next = TupleHelper.join(nextLeft, nextRight);
							nextRight = null;
						} else {


							int compareTo = nextLeft.compareTo(nextRight);

							if (compareTo < 0) {
//								if (discardedLeft != null) {
//									discardedLeft.push(nextLeft);
//								}
								if (leftIterator.hasNext()) {
									nextLeft = leftIterator.next();
								} else {
									nextLeft = null;
									break;
								}
							} else {
//								if (discardedRight != null) {
//									discardedRight.push(nextRight);
//								}
								if (rightIterator.hasNext()) {
									nextRight = rightIterator.next();
								} else {
									nextRight = null;
									break;
								}
							}

						}
					} else {
						return;
					}
				}


			}

			@Override
			public void close() throws SailException {
				leftIterator.close();
				rightIterator.close();
			}

			@Override
			public boolean hasNext() throws SailException {
				calculateNext();
				return next != null;
			}

			@Override
			public Tuple next() throws SailException {
				calculateNext();
				Tuple temp = next;
				next = null;
				return temp;
			}

			@Override
			public void remove() throws SailException {

			}

		};
	}

	@Override
	public int depth() {
		return 0;
	}

	@Override
	public void printPlan() {

	}

	@Override
	public String getId() {
		return null;
	}
}
