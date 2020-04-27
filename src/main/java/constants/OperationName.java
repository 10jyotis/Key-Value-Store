package constants;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class OperationName {

    public static final String PUT = "put";
    public static final String GET = "get";
    public static final String DEL = "del";
    public static final String STORE = "store";
    public static final String EXIT = "exit";

    // internal operations
    public static final String D_PUT1 = "dput1";
    public static final String D_PUT2 = "dput2";
    public static final String D_PUT_ABORT = "dputabort";
    public static final String D_DEL1 = "ddel1";
    public static final String D_DEL2 = "ddel2";
    public static final String D_DEL_ABORT = "ddelabort";

    public static final Set<String> PHASE_ONE_OPERATIONS = ImmutableSet.of(D_PUT1, D_PUT_ABORT, D_DEL1, D_DEL_ABORT);
    public static final Set<String> PHASE_TWO_OPERATIONS = ImmutableSet.of(D_PUT2, D_DEL2);
    public static final Set<String> INTERNAL_OPERATIONS = ImmutableSet
            .copyOf(Sets.union(PHASE_ONE_OPERATIONS, PHASE_TWO_OPERATIONS));
}
