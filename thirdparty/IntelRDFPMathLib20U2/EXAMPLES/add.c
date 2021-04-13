#include <stdio.h>
#include <stdlib.h>
#include <bid_conf.h>
#include <bid_functions.h>

int main(int argc, char *argv[]) {
	BID_UINT64 x;
	BID_UINT64 y;
	BID_UINT64 z;
	char buff[4096];

	if(argc!=3) {
		fprintf(stderr, "Usage: <A> <B>\n");
		return -1;
	}

	x = bid64_from_string(argv[1]);
	y = bid64_from_string(argv[2]);
	z = bid64_add(x, y);
	bid64_to_string(buff, x);
	printf("%s", buff);
	bid64_to_string(buff, y);
	printf(" + %s", buff);
	bid64_to_string(buff, z);
	printf(" = %s\n", buff);

	printf("%llu + %llu = %llu\n", x, y, z);
	return 0;
}
