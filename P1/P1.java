import java.util.*;

class Solution {

	static HashMap<Integer, HashMap<Character, ArrayList<Integer>>> enfa;
	static TreeSet<Integer> states;
	static ArrayList<Character> inputs;
	static ArrayList<Integer> finals;
	static HashMap<Integer, ArrayList<Integer>> eClosure;

	static void init() {
		enfa = new HashMap<Integer, HashMap<Character, ArrayList<Integer>>>();
		states = new TreeSet<Integer>();
		inputs = new ArrayList<Character>();
		finals = new ArrayList<Integer>();
		eClosure = new HashMap<Integer, ArrayList<Integer>>();
	}

	static void read() {
		Scanner sc = new Scanner(System.in);
		System.out.print("No. of transitions: ");
		int t = sc.nextInt();
		System.out.println("Transition input format:\n'<start_state> <input> <end_state>'");
		System.out.println("Epsilon input is denoted by 'E'");
		while(t-- > 0) {
			int s = sc.nextInt();
			char i = sc.next().charAt(0);
			int e = sc.nextInt();
			states.add(s);
			states.add(e);
			inputs.add(i);
			HashMap<Character, ArrayList<Integer>> hm = new HashMap<Character, ArrayList<Integer>>();
			ArrayList<Integer> al = new ArrayList<Integer>();
			if(enfa.containsKey(s)) {
				hm = enfa.get(s);
				if(hm.containsKey(i)) al = hm.get(i);
			}
			al.add(e);
			hm.put(i, al);
			enfa.put(s,hm);
		}
		System.out.print("Final states (separated by a single space): ");
		while(sc.hasNext()) finals.add(sc.nextInt());
		sc.close();
	}
	
	static void show() {
		for(Integer node : enfa.keySet()) {
			HashMap<Character, ArrayList<Integer>> temp = enfa.get(node);
			for(Character input : temp.keySet()) {
				System.out.println(node + "\t" + input + "\t" + temp.get(input));
			}
		}
	}
	
	static ArrayList<Integer> BFS(Integer start) {
		ArrayList<Integer> path = new ArrayList<Integer>();
		HashSet<Integer> visited = new HashSet<Integer>();
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(start);
		while(!queue.isEmpty()) {
			int node = (int)queue.poll();
			visited.add(node);
			//System.out.print(node + " ");
			path.add(node);
			try {
				ArrayList<Integer> al = enfa.get(node).get('E');
				for(Integer ch : al) {
					if(!visited.contains(ch)) {
						queue.add(ch);
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
		for(Integer i : states) {
			eClosure.put(i, BFS(i));
		}
		System.out.println("Epsilon closure of:");
		for(Integer i : eClosure.keySet()) System.out.println(i + "\t" + eClosure.get(i));
	}

	public static void main(String args[]) {
		init();
		read();
		//show();
		getEpsilonClosure();
	}
}
