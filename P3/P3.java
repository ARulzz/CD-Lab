import java.util.*;

class Solution {

	static HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>> nfa;
	static HashMap<HashSet<Integer>, HashMap<Character, HashSet<Integer>>> dfa;
	static HashSet<HashSet<Integer>> states;
	static HashSet<Character> inputs;
	static HashSet<Integer> start_state;
	static HashSet<HashSet<Integer>> finals;
	static HashMap<HashSet<Integer>, HashSet<HashSet<Integer>>> eClosure;

	static void init() {
		nfa = new HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>>();
		dfa = new HashMap<HashSet<Integer>, HashMap<Character, HashSet<Integer>>>();
		states = new HashSet<HashSet<Integer>>();
		inputs = new HashSet<Character>();
		start_state = new HashSet<Integer>();
		finals = new HashSet<HashSet<Integer>>();
		eClosure = new HashMap<HashSet<Integer>, HashSet<HashSet<Integer>>>();
	}

	static void read() {
		Scanner sc = new Scanner(System.in);
		System.out.print("No. of transitions: ");
		int t = sc.nextInt();
		System.out.println("Transition input format:\n'<start_state> <input> <end_state>'");
		System.out.println("Epsilon input is denoted by 'E'");
		while(t-- > 0) {
			HashSet<Integer> s = new HashSet<Integer>();
			s.add(sc.nextInt());
			char i = sc.next().charAt(0);
			HashSet<Integer> e = new HashSet<Integer>();
			e.add(sc.nextInt());

			states.add(s);
			states.add(e);
			inputs.add(i);

			HashMap<Character, HashSet<HashSet<Integer>>> hm = new HashMap<Character, HashSet<HashSet<Integer>>>();
			HashSet<HashSet<Integer>> al = new HashSet<HashSet<Integer>>();
			if(nfa.containsKey(s)) {
				hm = nfa.get(s);
				if(hm.containsKey(i)) al = hm.get(i);
			}
			al.add(e);
			hm.put(i, al);
			nfa.put(s,hm);
		}
		inputs.remove(Character.valueOf('E'));
		System.out.print("Start state: ");
		start_state.add(sc.nextInt());
		System.out.print("Final states (separated by a single space): ");
		sc.nextLine();
		String s[] = sc.nextLine().split(" ");
		for(String si : s) {
			HashSet<Integer> temp = new HashSet<Integer>();
			temp.add(Integer.parseInt(si));
			finals.add(temp);
		}
		sc.close();
		System.out.println("\n-----End of input-----\n");
	}

	static String getString(HashSet<Integer> hs) {
		String s = "";
		for(Integer i : hs) {
			s += i;
		}
		return s;
	}

	static String getString1(HashSet<HashSet<Integer>> hhs) {
		String s = "[";
		for(HashSet<Integer> i : hhs) {
			s += getString(i) + " ";
		}
		return s.trim().replace(" ", ", ") + "]";
	}
	
	static void showNfa() {
		System.out.println("\t   NFA\n");
		System.out.print("\t|");
		for(Character input : inputs) {
			System.out.print("\t" + input);
		}
		System.out.println();
		String op = "--------";
		System.out.print(op + "+");
		for(int i = 0; i < inputs.size(); i++) {
			System.out.print(op);
		}
		System.out.println(op);
		for(HashSet<Integer> node : states) {
			System.out.print(getString(node) + "\t|");
			HashMap<Character, HashSet<HashSet<Integer>>> temp = new HashMap<Character, HashSet<HashSet<Integer>>>();
			if(nfa.containsKey(node)) temp = nfa.get(node);
			for(Character input : inputs) {
				if(!temp.containsKey(input)) {
					System.out.print("\t-");
				}
				else {
					System.out.print("\t" + getString1(temp.get(input)));
				}
			}
			System.out.println();
		}
		System.out.println("\nStart state is: " + getString(start_state));
		System.out.println("Final states are: " + getString1(finals) + "\n");
	}

	static void showDfa() {
		System.out.println("\t   DFA\n");
		System.out.print("\t|");
		for(Character input : inputs) {
			System.out.print("\t" + input);
		}
		System.out.println();
		String op = "--------";
		System.out.print(op + "+");
		for(int i = 0; i < inputs.size(); i++) {
			System.out.print(op);
		}
		System.out.println(op);
		for(HashSet<Integer> node : states) {
			System.out.print(getString(node) + "\t|");
			HashMap<Character, HashSet<Integer>> temp = dfa.get(node);
			for(Character input : inputs) {
				System.out.print("\t" + getString(temp.get(input)));
			}
			System.out.println();
		}
		System.out.println("\nStart state is: " + getString(start_state));
		System.out.println("Final states are: " + getString1(finals) + "\n");
	}
	
	static HashSet<HashSet<Integer>> BFS(HashSet<Integer> start) {
		HashSet<HashSet<Integer>> path = new HashSet<HashSet<Integer>>();
		HashSet<HashSet<Integer>> visited = new HashSet<HashSet<Integer>>();
		Queue<HashSet<Integer>> queue = new LinkedList<HashSet<Integer>>();
		queue.add(start);
		while(!queue.isEmpty()) {
			HashSet<Integer> node = queue.poll();
			visited.add(node);
			path.add(node);
			try {
				HashSet<HashSet<Integer>> hs = nfa.get(node).get('E');
				for(HashSet<Integer> i : hs) {
					if(!visited.contains(i)) {
						queue.add(i);
					}
				}
			}
			catch(Exception e) {
				continue;			
			}
		}
		return path;
	}

	static void getEpsilonClosure() {
		eClosure = new HashMap<HashSet<Integer>, HashSet<HashSet<Integer>>>();
		for(HashSet<Integer> i : states) {
			HashSet<HashSet<Integer>> temp = new HashSet<HashSet<Integer>>();
			for(Integer j : i) {
				temp.addAll(BFS(i));
			}
			eClosure.put(i, BFS(i));
		}
		//for(HashSet<Integer> i : eClosure.keySet()) System.out.println(i.toString().replace("[", "").replace("]", "") + "\t" + eClosure.get(i).toString().replace("[", "").replace("]", ""));
	}

	static void enfaToNfa() {
		HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>> temp_nfa = new HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>>();
		for(HashSet<Integer> i : states) {
			HashSet<HashSet<Integer>> temp = eClosure.get(i);
			for(HashSet<Integer> j : temp) {
				if(finals.contains(j)) finals.add(i);
				for(Character ch : inputs) {
					if(!nfa.containsKey(j) || !nfa.get(j).containsKey(ch)) continue; 
					HashSet<HashSet<Integer>> temp1 = nfa.get(j).get(ch);
					for(HashSet<Integer> k : temp1) {
						HashSet<HashSet<Integer>> temp2 = eClosure.get(k);
						for(HashSet<Integer> l : temp2) {
							HashMap<Character, HashSet<HashSet<Integer>>> hm = new HashMap<Character, HashSet<HashSet<Integer>>>();
							HashSet<HashSet<Integer>> al = new HashSet<HashSet<Integer>>();
							if(temp_nfa.containsKey(i)) {
								hm = temp_nfa.get(i);
								if(hm.containsKey(ch)) al = hm.get(ch);
							}
							al.add(l);
							hm.put(ch, al);
							temp_nfa.put(i,hm);
						}
					}
				}
			}
		}
		nfa = temp_nfa;
	}

	static void nfaToDfa() {
		HashSet<HashSet<Integer>> temp_states = new HashSet<HashSet<Integer>>();
		HashSet<HashSet<Integer>> temp_finals = new HashSet<HashSet<Integer>>();
		Queue<HashSet<Integer>> q = new LinkedList<HashSet<Integer>>();
		q.add(start_state);
		while(!q.isEmpty()) {
			HashSet<Integer> state = q.poll();
			temp_states.add(state);
			for(Character ch : inputs) {
				HashSet<Integer> temp = new HashSet<Integer>();
				for(Integer i : state) {
					HashSet<Integer> temp_key = new HashSet<Integer>();
					temp_key.add(i);
					try {
						for(HashSet<Integer> j : nfa.get(temp_key).get(ch))
							temp.addAll(j);
					}
					catch(Exception e) {
						continue;
					}
				}
				if(temp.isEmpty()) temp.add(99);
				HashMap<Character, HashSet<Integer>> trans = new HashMap<Character, HashSet<Integer>>();
				if(dfa.containsKey(state)) {
					trans = dfa.get(state);
				}
				trans.put(ch, temp);
				dfa.put(state, trans);
				if(!temp_states.contains(temp)) q.add(temp);
				for(HashSet<Integer> i :finals) {
					boolean flag = false;
					for(Integer j : i) {
						if(temp.contains(j)) {
							flag = true;
							temp_finals.add(temp);
							break;
						}
					}
					if(flag) break;
				}
			}
		}
		states = temp_states;
		finals = temp_finals;
	}

	public static void main(String args[]) {
		init();
		read();
		getEpsilonClosure();
		enfaToNfa();
		// showNfa();
		nfaToDfa();
		showDfa();
	}
}
