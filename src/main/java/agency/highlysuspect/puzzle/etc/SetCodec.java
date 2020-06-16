package agency.highlysuspect.puzzle.etc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;

import java.util.HashSet;
import java.util.Set;

/** Quick and dirty Codec for a HashSet. Pretty lazy, just uses a list underneath and converts it to a set afterwards. */
public class SetCodec<A> implements Codec<Set<A>> {
	public SetCodec(Codec<A> elementCodec) {
		this.elementCodec = elementCodec;
	}
	
	private final Codec<A> elementCodec;
	
	@Override
	public <T> DataResult<T> encode(Set<A> input, DynamicOps<T> ops, T prefix) {
		//A cut-and-paste of ListCodec. (I can't delegate without copying the set into a list, so paste it is.)
		ListBuilder<T> listBuilder = ops.listBuilder();
		input.forEach(a -> listBuilder.add(elementCodec.encodeStart(ops, a)));
		return listBuilder.build(prefix);
	}
	
	@Override
	public <T> DataResult<Pair<Set<A>, T>> decode(DynamicOps<T> ops, T input) {
		//The lazy way out...
		return elementCodec.listOf().decode(ops, input).map(p -> Pair.of(new HashSet<>(p.getFirst()), p.getSecond()));
	}
}
